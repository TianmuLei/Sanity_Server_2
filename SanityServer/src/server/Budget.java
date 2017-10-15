package server;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Budget {
	String email, budgetName,date;
	Integer period;
	Integer budgetTotal;
	ArrayList<Category> categories;
	public Budget(JSONObject JSONMessage){
		try{
			email=JSONMessage.getString("email");
			budgetName=JSONMessage.getString("name");
			date=JSONMessage.getString("date");
			period=JSONMessage.getInt("period");
			budgetTotal= JSONMessage.getInt("budgetTotal");
			JSONArray jArray=JSONMessage.getJSONArray("categories");
			for(int i=0;i<jArray.length();++i){
				categories.add(new Category((JSONObject)jArray.get(i)));//cast object into JSONObject
			}
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("contructing budget Error");
		}
	}
}