package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

public class BudgetDAO extends DAO{
	CategoryDAO CateDAO;
	public BudgetDAO(){
		CateDAO = new CategoryDAO();
	}
	public JSONObject createBudget(Budget toAdd){	
		try{
			JSONObject message = new JSONObject();
			message.put("function", "createBudget");	
			if(checkBudgetExist(toAdd)){
				message.put("status", "fail");
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
		int user_id=-1;
		PreparedStatement userStatement = conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=?");
		userStatement.setString(1, toAdd.email);
		try{
			ResultSet rs= userStatement.executeQuery();
			rs.next();
			user_id=rs.getInt("User_id");
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("find user id error(add budget)");
		}finally{
			if (userStatement != null) {
				userStatement.close();
			}
		}
		PreparedStatement st = conn.prepareStatement("SELECT * FROM SanityDB.Budget WHERE Budget_name=? "
				+ "AND User_id=?");// there cannot be two same name budget
		st.setString( 1, toAdd.budgetName);
		st.setInt(2, user_id);
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
		
		int user_id=-1;
		PreparedStatement userStatement = conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=?");
		userStatement.setString(1, toAdd.email);
		try{
			ResultSet rs= userStatement.executeQuery();
			rs.next();
			user_id=rs.getInt("User_id");
			toAdd.userId=user_id;
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("find user error(add budget)");
		}finally{
			if (userStatement != null) {
				userStatement.close();
			}
		}	
		PreparedStatement st =  
				conn.prepareStatement("INSERT INTO SanityDB.Budget (Budget_name, "
						+ "User_id,Budget_period,Start_date,Budget_total,Budget_spent) VALUE(?,?,?,?,?,?)");
		st.setString( 1, toAdd.budgetName);
		st.setInt( 2, user_id);
		st.setInt(3, toAdd.period);
		st.setDate(4, java.sql.Date.valueOf(toAdd.date));
		st.setInt(5, toAdd.budgetTotal);
		st.setInt(6, 0);
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
}
