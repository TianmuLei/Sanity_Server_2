package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CategoryDAO extends DAO{
	public void addCategory(Budget budget){//add category from addBudget
		try{
			insertCategory(budget);
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("Error in insertCategory");
		}		
	}
	
	public JSONObject addSingleCategory(String email, String budgetName,String categoryName,
			Double limit){
		JSONObject returnMessage = new JSONObject();
		try{
			Connection conn=getDBConnection();
			PreparedStatement getID = conn.prepareStatement("SELECT * FROM SanityDB.Sanity_category WHERE "
					+ "Email=? AND Budget_name =?");
			getID.setString(1, email);
			getID.setString(2, budgetName);
			ResultSet resultSet=getID.executeQuery();
			Integer userID=-1;
			Integer budgetID=-1;
			if(resultSet.next()){
				userID=resultSet.getInt("User_id");
				budgetID=resultSet.getInt("Budget_id");
			}
			if(resultSet!=null){
				resultSet.close();
			}
			if(getID!=null){
				getID.close();
			}
			PreparedStatement insert = conn.prepareStatement("INSERT INTO SanityDB.Category(User_id, Category_name,"
					+ "Budget_id,Category_total,Category_spent) VALUE(?,?,?,?,?)");
			insert.setInt(1, userID);
			insert.setString(2, categoryName);
			insert.setInt(3, budgetID);
			insert.setDouble(4, limit);
			insert.setDouble(5, 0);
			insert.executeUpdate();
			if(insert!=null){
				insert.close();
			}
			if(conn!=null){
				conn.close();
			}
			returnMessage.put("status", "success");
		}catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("addSingleCategory SQL Error");
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("addSingleCategory JSON Error ");
		}
		return returnMessage;
	}
	
	public JSONObject getCategories(User user, Budget budget){
		JSONObject returnMessage = new JSONObject();
		try{
			JSONArray array=getCategoriesListDB(user, budget);	
			JSONObject information=new JSONObject();
			information.put("categoryList", array);
			information.put("budgetName", budget.budgetName);
			returnMessage.put("function", "returnCategoryList");
			returnMessage.put("status", "success");
			returnMessage.put("information", information);		
		}catch (JSONException e) {
			System.out.println(e.getMessage());
			System.out.println("JSON Error in getCategory");
		}
		return returnMessage;	
	}
	
	public JSONObject deleteCategories(String email,String budgetName, String categoryName){
		JSONObject returnMessage = new JSONObject();
		try{
			returnMessage.put("function", "deleteCategory");
			deleteCategoryDB(email,budgetName,categoryName);
			returnMessage.put("status", "success");
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("JSON error in deleteCategory");
		}
		return returnMessage;
	}
	public JSONObject editCategory(String email, String oldName, String newName, String budgetName,
			Double newLimit){
		JSONObject returnMessage = new JSONObject();
		try{
			returnMessage.put("function", "editCategory");
			if(editCategoryDB(email,oldName,newName,budgetName,newLimit)){
				returnMessage.put("status", "success");
				System.out.println("Success");
			}
			else{
				returnMessage.put("status", "fail");
				returnMessage.put("detail", "duplicated category name");
				System.out.println("fail");
			}
			
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("JSON error in editCategory");
		}
		return returnMessage;
	}
	private Boolean editCategoryDB(String email, String oldName, String newName, String budgetName,
			Double newLimit){
		Connection conn =getDBConnection();
		try{
			PreparedStatement checkCategory = conn.prepareStatement("SELECT * FROM SanityDB.Sanity_category WHERE Category_name=? "
					+ "AND Email=? AND Budget_name=?");
			checkCategory.setString(1, oldName);
			checkCategory.setString(2, email);
			checkCategory.setString(3, budgetName);
			ResultSet check = checkCategory.executeQuery();
			if(check.wasNull()){
				return false;
			}
			if(check!=null){
				check.close();
			}
			System.out.println(1);
			if(checkCategory!=null){
				checkCategory.close();
			}
			PreparedStatement getCategory = conn.prepareStatement("SELECT * FROM SanityDB.Sanity_category WHERE Category_name=? "
					+ "AND Email=? AND Budget_name=?");
			getCategory.setString(1, oldName);
			getCategory.setString(2, email);
			getCategory.setString(3, oldName);
			System.out.println(2);
			ResultSet resultSet=getCategory.executeQuery();
			
			System.out.println(2);
			Integer categoryID =-1;
			if(resultSet.next()){
				categoryID=resultSet.getInt("Category_id");
			}
			PreparedStatement updateCategory = conn.prepareStatement("UPDATE SanityDB.Category SET Category_name=?"
					+ ",Category_total=? WHERE Category_id=?");
			updateCategory.setString(1, newName);
			updateCategory.setDouble(2, newLimit);
			updateCategory.setInt(3, categoryID);
			updateCategory.executeUpdate();
			System.out.println(3);
			if(updateCategory!=null){
				updateCategory.close();
			}
			if(conn!=null){
				conn.close();
			}
		}catch (SQLException e) {
			System.out.println("SQL Error in editCategoryDB");
			System.out.println(e.getMessage());
		}
		return true;	
	}
	private void insertCategory(Budget budget) throws SQLException{
		Connection conn = getDBConnection();
		BudgetFindBudgetID(budget);
		for(int i=0;i<budget.categories.size();++i){
			PreparedStatement st = conn.prepareStatement("INSERT INTO SanityDB.Category(User_id, Category_name, Budget_id,"
					+ "Category_total, Category_spent) VALUE(?,?,?,?,?)");
			st.setInt(1, budget.userId);
			st.setString(2,budget.categories.get(i).categoryName);
			st.setInt(3, budget.budgetId);
			st.setInt(4, budget.categories.get(i).limit);
			st.setInt(5,0);
			try{
				st.executeUpdate();
			}catch(SQLException e){
				System.out.println(e.getMessage());
				System.out.println("insert error(add categories)");
			}finally{
				if (st != null) {
					st.close();
				}
			}
		}
		if(conn!=null){
			conn.close();
		}
	}
	
	
}
