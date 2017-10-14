package server;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	String username;
	String email;
	String password1;
	String password2;
	public User(String userName, String email, String psword, String psword2){
		username=userName;
		this.email=email;
		password1=psword;
		password2=psword2;
	}
	
	public User(JSONObject JSONMessage){
		try{
			username=JSONMessage.getString("username");
			email=JSONMessage.getString("Email");
			password1=JSONMessage.getString("password1");
			password2=JSONMessage.getString("password2");
		}catch(JSONException e){
			System.out.println(e.getMessage());
		}
		
	}
}

	
	
	/*private String username;
	private String email;
	private String password1;
	private String password2;
	private String func;
	public User(){
		
	}
	public void setFunc(String a){
		this.func = a;
	}
	public void setUsername(String a){
		this.username = a;
	}
	public void setEmail(String a){
		this.email = a;
	}
	public void setPassword1(String a){
		this.password1 = a;
	}
	public void setPassword2(String a){
		this.password2 = a;
	}
	public String getUsername(){
		return this.username;
	}
	public String getEmail(){
		return this.email;
	}
	public String getFunc(){
		return this.func;
	}
	public String getPwd1(){
		return this.password1;
	}
	public String getPwd2(){
		return this.password2;
	}*/