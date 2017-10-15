package server;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	String username;
	String email;
	String password1;
	String password2;
	public User(String userName, String email, String psword, String psword2){
		this.username=userName;
		this.email=email;
		this.password1=psword;
		this.password2=psword2;
	}
	
	public User(JSONObject JSONMessage){
		try{
			username=JSONMessage.getString("username");
			email=JSONMessage.getString("email");
			password1=JSONMessage.getString("password1");
			password2=JSONMessage.getString("password2");
			//System.out.println("get email"+email);
		}catch(JSONException e){
			System.out.println(e.getMessage());
		}	
	}
}
