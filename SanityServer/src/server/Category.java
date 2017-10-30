package server;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {
	String categoryName;
	Integer limit;
	Integer budgetID;
	Integer categoryID;
	Integer userID;
	
	
	public Category(JSONObject JSONMessage){
		try{
			categoryName=JSONMessage.getString("name");
			limit = JSONMessage.getInt("limit");
			budgetID=-1;
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("parseJSON error(category constuctor)");
		}
	}
	public Category(String cateName){
		categoryName=cateName;
		limit=-1;
		budgetID=-1;
		userID=-1;
	}
	public Category(String cateName, int limit){
		categoryName=cateName;
		this.limit=limit;
	}
}
