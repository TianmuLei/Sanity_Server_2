package server;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {
	String categoryName;
	Integer limit;
	public Category(JSONObject JSONMessage){
		try{
			categoryName=JSONMessage.getString("name");
			limit = JSONMessage.getInt("limit");
		}catch(JSONException e){
			System.out.println(e.getMessage());
		}
	}
}
