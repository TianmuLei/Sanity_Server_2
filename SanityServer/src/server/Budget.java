package server;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Budget {
	String email, budgetName,date;
	Integer period;
	Double budgetTotal;
	ArrayList<Category> categories;
	Integer userId=-1;
	Integer budgetId=-1;
	Integer frequency=-1;
	Integer threshold = -1;
	Integer requestPeriod=-1;
	String realStartDate;
	String realEndDate;
	public Budget(JSONObject JSONMessage){
		try{
			categories = new ArrayList<Category>();
			email=JSONMessage.getString("email");
			budgetName=JSONMessage.getString("name");
			date=JSONMessage.getString("date");
			period=JSONMessage.getInt("period");
			budgetTotal= JSONMessage.getDouble("budgetTotal");
			frequency = JSONMessage.getInt("frequency");
			threshold = JSONMessage.getInt("threshold");
			JSONArray jArray=JSONMessage.getJSONArray("categories");
			for(int i=0;i<jArray.length();++i){
				categories.add(new Category((JSONObject)jArray.get(i)));//cast object into JSONObject
			}
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("contructing budget Error");
		}
	}
	public Budget(JSONObject JSONMessage,Integer requestPeriod){
		try{
			//categories = new ArrayList<Category>();
			//email=JSONMessage.getString("email");
			budgetName=JSONMessage.getString("name");
			date=JSONMessage.getString("date");
			period=JSONMessage.getInt("period");
			budgetTotal= JSONMessage.getDouble("budgetTotal");
			frequency = JSONMessage.getInt("frequency");
			threshold = JSONMessage.getInt("threshold");
			this.requestPeriod=requestPeriod;
		
			
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("contructing budget Error");
		}
	}
	
	
	
	public Budget(String name) {
		budgetName=name;
		
	}
}
