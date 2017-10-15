package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.json.*;


public class UserDAO {
	
	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_CONNECTION = "jdbc:mysql://127.0.0.1/SanityDB?user=root&password=chenyang&useSSL=false";
	private static final String DB_USER = "user";
	private static final String DB_PASSWORD = "password";

	
	public static JSONObject Register(User u){
		
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
	
	public static boolean checkExisttWithEmail(String Email) throws SQLException{
		
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
	
	
	public static boolean login(String Email,String ps1,String ps2) throws SQLException{
		
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
	
	
	
public static void addUser(User user) throws SQLException{
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
			 st.executeQuery();
			
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
	
	/*
	public static JSONObject Register(JSONObject a){
		User toadd = new User();
		JSONObject toret = new JSONObject();
		
		JSONObject arr;
		try {
			toadd.setFunc(a.getString("function"));
			arr = a.getJSONObject("information");
			toadd.setUsername(arr.getString("username"));
			toadd.setEmail(arr.getString("Email"));
			toadd.setPassword1(arr.getString("password1"));
			toadd.setPassword2(arr.getString("password2"));
			toret = addUserToDB(toadd);
			
		}	
		 catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toret;
	}
	public static JSONObject login(JSONObject a){
		User toadd = new User();
		JSONObject toret = new JSONObject();
		
		JSONObject arr;
		try {
			toadd.setFunc(a.getString("function"));
			arr = a.getJSONObject("information");
			toadd.setUsername(arr.getString("username"));
			toadd.setPassword1(arr.getString("password1"));
			toadd.setPassword2(arr.getString("password2"));
			toadd.setEmail(arr.getString("Email"));
			
			toret = addUserToDB(toadd);
			
		}	
		 catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toret;
	}
	public static JSONObject addUserToDB(User toadd){
		JSONObject toret = new JSONObject ();
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/SanityDB?user=root&password=chenyang&useSSL=false");
			PreparedStatement st = 
					conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=?");
			st.setString( 1, toadd.getEmail());
			System.out.println(st);

			ResultSet rs = st.executeQuery();
			if(rs.next()){
				toret.put("function", toadd.getFunc());
				toret.put("status", "fail");
				JSONObject info = new JSONObject();
				info.put("reason", "Username already exist");
				toret.put("information", info);
				
			}
			ResultSet rs = st.executeQuery();
			if(rs.next()){
				toret.put("function", toadd.getFunc());
				toret.put("status", "fail");
				JSONObject info = new JSONObject();
				info.put("reason", "Username already exist");
				toret.put("information", info);
				
			}
			st = 
					conn.prepareStatement("INSERT INTO SanityDB.User (Username, Email, Password1, Password2) VALUE ('"+toadd.getUsername()+
					"','"+toadd.getEmail()+"','"+toadd.getPwd1()+"','"+toadd.getPwd2()+"')");
			st.execute();
			toret.put("function", toadd.getFunc());
			toret.put("status", "success");
		}
		catch(SQLException sqle){
			System.out.println(sqle.getMessage());
		}
		catch(ClassNotFoundException cnfe){
			System.out.println(cnfe.getMessage());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toret;
	}
	public static JSONObject loginDAO(User toadd){
		JSONObject toret = new JSONObject ();
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/SanityDB?user=root&password=chenyang&useSSL=false");
			PreparedStatement st = 
					
					conn.prepareStatement("SELECT FROM SanityDB.User WHERE Email = '"+ toadd.getEmail()+"'");
			ResultSet rs = st.executeQuery();
			if(rs.next()){
				toret.put("function", toadd.getFunc());
				toret.put("status", "fail");
				JSONObject info = new JSONObject();
				info.put("reason", "Username already exist");
				toret.put("information", info);
				
			}
			st = 
					conn.prepareStatement("INSERT INTO SanityDB.User (Username, Email, Password1, Password2) VALUE ('"+toadd.getUsername()+
					"','"+toadd.getEmail()+"','"+toadd.getPwd1()+"','"+toadd.getPwd2()+"')");
			st.execute();
			toret.put("function", toadd.getFunc());
			toret.put("status", "success");
		}
		catch(SQLException sqle){
			System.out.println(sqle.getMessage());
		}
		catch(ClassNotFoundException cnfe){
			System.out.println(cnfe.getMessage());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toret;
	}*/
	
	
	private static Connection getDBConnection() {

		Connection dbConnection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {
			dbConnection= DriverManager.getConnection("jdbc:mysql://127.0.0.1/SanityDB?user=root&password=chenyang&useSSL=false");		
			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return dbConnection;

	}
	
}