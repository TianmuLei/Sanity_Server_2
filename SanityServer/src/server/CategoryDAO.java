package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;

public class CategoryDAO extends DAO{
	public void addCategory(Budget budget){
		try{
			insertCategory(budget);
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("Error in insertCategory");
		}		
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
			dropCategory(email,budgetName,categoryName);
			returnMessage.put("status", "success");
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("JSON error in deleteCategory");
		}
		return returnMessage;
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
	
	private void dropCategory(String email, String budgetName, String categoryName){
		try{
			Connection conn= getDBConnection();
			PreparedStatement getCategoryID=conn.prepareStatement("SELECT * FROM SanityDB.Sanity_category WHERE Email=? AND"
					+ " Budget_name=? AND Category_name=?");
			getCategoryID.setString(1, email);
			getCategoryID.setString(2, budgetName);
			getCategoryID.setString(3, categoryName);
			ResultSet resultSet=getCategoryID.executeQuery();
			Integer categoryID=-1;
			if(resultSet.next()){
				categoryID = resultSet.getInt("Category_id");
			}
			if(resultSet!=null){
				resultSet.close();
			}
			if(getCategoryID!=null){
				getCategoryID.close();
			}
			PreparedStatement deleteCategory = conn.prepareStatement("DELETE FROM SanityDB.Category WHERE Category_id=?");
			deleteCategory.setInt(1, categoryID);
			deleteCategory.executeUpdate();
			if(deleteCategory!=null){
				deleteCategory.close();
			}
			if(conn!=null){
				conn.close();
			}
		}catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("drop category SQL error");
		}
	}
}
