package server;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class Budget {
	String email, budgetName,date;
	Integer period;
	ArrayList<Category> categories;
	public Budget(JSONObject JSONMessage){
		try{
			email=JSONMessage.getString("email");
			budgetName=JSONMessage.getString("name");
			date=JSONMessage.getString("date");
			period=Integer.parseInt(JSONMessage.getString("period"));
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("contructing budget");
		}
	}
}
