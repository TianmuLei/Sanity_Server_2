package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.json.*;


public class UserDAO {
	
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
	
}