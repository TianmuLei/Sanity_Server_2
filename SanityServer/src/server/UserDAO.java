package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;


public class UserDAO extends DAO{
	
	public JSONObject Register(User u){	
		try{		
			JSONObject message = new JSONObject();
			message.put("function", "register");	
			if(checkUserExist(u)){
				message.put("status", "fail");
			}
			else{
				addUser(u);
				System.out.println("return false");
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
	public JSONObject Login(User u){
		try{		
			JSONObject message = new JSONObject();
			message.put("function", "login");	
			if(verifyPassword(u.email,u.password1,u.password2)){
				message.put("status", "success");
				String username = getUsername(u.email);
				message.put("username", username);
			}
			else{
				System.out.println("return false");
				message.put("status", "fail");
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
	public JSONObject autoLogin(String email){
		JSONObject message= new JSONObject();
		try{
			message.put("function", "autoLogin");
			if(checkUserExist(new User(email))){
				message.put("status", "success");
				String username = getUsername(email);
				message.put("username", username);
			}
			else{
				message.put("status", "fail");
				message.put("detail", "user does not exist");
			}
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("JSON error in auto login");
		}catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("SQL error in auto login");
		}
		return message;
	}
	
	public boolean checkUserExist(User user) throws SQLException{
		if(UserFindUserID(user)==-1){
			return false;
		}
		return true;
	}
	private String getUsername(String email){
		String toReturn ="";
		try{
			Connection conn=getDBConnection();
			PreparedStatement statement= conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=?");
			statement.setString(1, email);
			ResultSet rs= statement.executeQuery();
			rs.next();
			toReturn=rs.getString("Username");
			if(statement!=null){
				statement.close();
			}
			if(conn!=null){
				conn.close();
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("getUsername Error with email");
		}
		return toReturn;
	}
	private boolean verifyPassword(String Email,String ps1,String ps2) throws SQLException{
		Connection conn=getDBConnection();
		PreparedStatement st = conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=? AND Password1=? AND Password2=?");
		st.setString( 1, Email);
		st.setString( 2, ps1);
		st.setString( 3, ps2);
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
	
	public void addUser(User user) throws SQLException{
		System.out.println("add user");
		Connection conn=getDBConnection();
		System.out.println(DAO.testing);
		PreparedStatement st =  
				conn.prepareStatement("INSERT INTO SanityDB.User (Username, Email, Password1, Password2) VALUE (?,?,?,?)");
		st.setString( 1, user.username);
		st.setString( 2, user.email);
		st.setString( 3, user.password1);
		st.setString( 4, user.password2);
		try{
			 st.executeUpdate();		
		}catch(SQLException e){
			System.out.println("excaption at addUser");
			System.out.println(e.getMessage());
		}finally{
			if (conn != null) {
				conn.close();
			}
			if (st != null) {
				st.close();
			}
		}		
	}
	
	public JSONObject changeUsername(User user) throws SQLException{
		System.out.println("change Username");
		Connection conn=getDBConnection();
		JSONObject message = new JSONObject();
		try {
			message.put("function", "changeUsername");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try{
			PreparedStatement upUser = conn.prepareStatement("UPDATE  SanityDB.User SET Username= ? WHERE Email = ?");
			upUser.setString( 1, user.username);
			upUser.setString( 2, user.email);
			upUser.execute();
			message.put("status", "success");	
		}catch(SQLException e){
			System.out.println(e.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			if (conn != null) {
				conn.close();
			}	
		}
		return message;		
	}
	public JSONObject changePassword(User user1, User user2) throws SQLException{
		System.out.println("change password");
		Connection conn=getDBConnection();
		JSONObject message = new JSONObject();
		try {
			message.put("function", "changePassword");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try{
			 if(verifyPassword(user1.email, user1.password1, user1.password2)){
			 	PreparedStatement upUser = conn.prepareStatement("UPDATE SanityDB.User SET Password1= ?,Password2 = ? WHERE Email = ?");
			 	System.out.println(user2.password1 + user2.password2);
				upUser.setString( 1, user2.password1);
				upUser.setString( 2, user2.password2);
				upUser.setString( 3, user2.email);
				upUser.executeUpdate();
				message.put("status", "success");
			 }	
			 else{
			 	message.put("status", "fail");
			 	message.put("detail", "incorrect password");
			 }	
		}catch(SQLException e){
			System.out.println(e.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
		}finally{
			if (conn != null) {
				conn.close();
			}	
		}
		return message;		
	}
}
	
