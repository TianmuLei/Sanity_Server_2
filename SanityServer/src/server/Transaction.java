package server;

import org.json.JSONException;
import org.json.JSONObject;

public class Transaction {
	String email, description, category, budget, date;
	Double amount;
	Integer userID=-1, budgetID=-1, categoryID=-1;
	public Transaction(JSONObject message){
		try{
			email= message.getString("email");
			description = message.getString("description");
			category = message.getString("category");
			budget = message.getString("budget");
			date =message.getString("date");
			amount = message.getDouble("amount");
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("constructing transaction error");
		}		
	}
	public Transaction(String email, String description, String category, String budget, String date, double amound){
		this.email = email;
		this.description = description;
		this.category = category;
		this.budget = budget;
		this.date = date;
		this.amount = amound;
	}
}
