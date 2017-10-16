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
	public JSONArray getCategoriesList(User user, Budget budget){
		JSONArray Jarray=new JSONArray();
		PreparedStatement findAllCategory;
		try{
			Integer user_id=UserFindUserID(user);
			budget.userId=user_id;
			BudgetFindBudgetID(budget);
			Integer budgetID=budget.budgetId;
			Connection conn=getDBConnection();
			findAllCategory= conn.prepareStatement("SELECT * FROM SanityDB.Category WHERE User_id=? AND Budget_id=?");
			findAllCategory.setInt(1, user_id);
			findAllCategory.setInt(2, budgetID);
			ResultSet rs =findAllCategory.executeQuery();
			while(rs.next()){
				JSONObject temp = new JSONObject();
				temp.put("name", rs.getString("Category_name"));
				temp.put("limit", rs.getDouble("Category_total"));
				temp.put("categorySpent", rs.getDouble("Category_spent"));
				Jarray.put(temp);
			}
			if (findAllCategory != null) {
				findAllCategory.close();
			}
			if(conn!=null){
				conn.close();
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("Error in getCategory");
		}catch (JSONException e) {
			System.out.println(e.getMessage());
			System.out.println("JSON Error in getCategory");
		}		
		return Jarray;
	}
	
	public JSONObject getCategories(User user, Budget budget){
		JSONObject returnMessage = new JSONObject();
		try{
			JSONArray array=getCategoriesList(user, budget);	
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
