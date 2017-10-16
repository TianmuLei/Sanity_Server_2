package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BudgetDAO extends DAO{
	CategoryDAO CateDAO;
	public BudgetDAO(){
		CateDAO = new CategoryDAO();
	}

	public JSONObject getBudgetList(User user) {
		JSONObject returnMessage = new JSONObject();
		try{
			JSONObject information = new JSONObject();
			JSONArray jsonArray=getBudgetListDB(user);
			information.put("budgetList", jsonArray);
			returnMessage.put("function", "returnBudgetList");
			returnMessage.put("status", "success");
			returnMessage.put("information", information);		
		}catch(SQLException e){
			System.out.println("getBudgetList error");
		}catch(JSONException e){
			System.out.println("getBudgetList error");
		}
		return returnMessage;	
	}
	
	
	public JSONObject editBudget(Budget toEdit, Budget original){
		try{
			JSONObject message = new JSONObject();
			message.put("function", "editBudget");
			if(editBudgetDB(toEdit,original)){
				message.put("status", "success");
			}
			else{
				message.put("status", "fail");
			}
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}catch (JSONException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}	
		return null;
	}
	public JSONObject createBudget(Budget toAdd){	
		try{
			JSONObject message = new JSONObject();
			message.put("function", "createBudget");// we need give more info to this message	
			if(checkBudgetExist(toAdd)){
				message.put("status", "fail");
				message.put("detail", "budget with same name already exist");
			}
			else{
				addBudgetDB(toAdd);
				CateDAO.addCategory(toAdd);
				System.out.println("return success");
				message.put("status", "success");
			}	
			return message;
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}catch (JSONException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}	
		return null;
	}
	
	private Boolean checkBudgetExist(Budget toAdd) throws SQLException{
		Connection conn=getDBConnection();
		BudgetFindUserID(toAdd);
		PreparedStatement st = conn.prepareStatement("SELECT * FROM SanityDB.Budget WHERE Budget_name=? "
				+ "AND User_id=?");// there cannot be two same name budget
		st.setString( 1, toAdd.budgetName);
		st.setInt(2, toAdd.userId);
		try{
			ResultSet rs = st.executeQuery();
			if(rs.next()){
				return true;
			}
			else{
				return false;
			}	
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("check budget exist error(add budget)");
		}finally{
			if (conn != null) {
				conn.close();
			}
			if (st != null) {
				st.close();
			}
		}
		return true;
	}
	private void addBudgetDB(Budget toAdd) throws SQLException{
		System.out.println("add user");
		Connection conn=getDBConnection();
		BudgetFindUserID(toAdd);
		PreparedStatement st =  
				conn.prepareStatement("INSERT INTO SanityDB.Budget (Budget_name, "
						+ "User_id,Budget_period,Start_date,Budget_total,Budget_spent,Frequency, Threshold) VALUE(?,?,?,?,?,?,?,?)");
		st.setString( 1, toAdd.budgetName);
		st.setInt( 2, toAdd.userId);
		st.setInt(3, toAdd.period);
		st.setDate(4, java.sql.Date.valueOf(toAdd.date));
		st.setDouble(5, toAdd.budgetTotal);
		st.setDouble(6, 0);
		st.setInt(7, toAdd.frequency);
		st.setInt(8, toAdd.threshold);
		try{
			 st.executeUpdate();		
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("insert error(add budget)");
		}finally{
			if (conn != null) {
				conn.close();
			}
			if (st != null) {
				st.close();
			}
		}
	}
	private Boolean editBudgetDB(Budget toEdit, Budget original) throws SQLException{
		Connection conn=getDBConnection();
		// find the original budget and then update;
		PreparedStatement updateStatement = conn.prepareStatement("UPDATE SanityDB.Budget SET Budget_name=?"
				+ ",User_id=?,Budget_period=?,Start_date=?,Budget_total=?,Budget_spent=?,Frequency=?,Threshold=?");
		return false;
	}
}
