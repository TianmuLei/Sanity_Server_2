package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.json.*;


public class UserDAO extends DAO{
	
	public JSONObject Register(User u){	
		try{		
			JSONObject message = new JSONObject();
			message.put("function", "register");	
			if(checkExisttWithEmail(u.email)){
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
				message.put("status", "sucess");
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
	
	public boolean checkExisttWithEmail(String Email) throws SQLException{
		Connection conn=getDBConnection();
		PreparedStatement st = conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=?");
		st.setString( 1, Email);
		try{
			ResultSet rs = st.executeQuery();
			
			System.out.println("check exitst");
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
		PreparedStatement st =  
				conn.prepareStatement("INSERT INTO SanityDB.User (Username, Email, Password1, Password2) VALUE (?,?,?,?)");
		st.setString( 1, user.username);
		st.setString( 2, user.email);
		st.setString( 3, user.password1);
		st.setString( 4, user.password2);
		st.execute();
		try{
			 st.executeUpdate();		
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
	}
	
	

	
}