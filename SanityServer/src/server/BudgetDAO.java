package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BudgetDAO extends DAO{
	CategoryDAO CateDao;
	public BudgetDAO(){
		CateDao = new CategoryDAO();
	}
	public JSONObject getEverything(User user,Integer period){
		JSONObject returnMessage = new JSONObject();
		try{
			JSONArray budgetList=getBudgetListDB(user);
			System.out.println(budgetList.length());
			for(int i=0;i<budgetList.length();++i){
				JSONObject budgetJSON=budgetList.getJSONObject(i);
				Budget budget=new Budget(budgetJSON,period);
				budget.email=user.email;
				JSONArray categoryList= getCategoriesListDB(user,budget);
				for(int j=0;j<categoryList.length();j++){
					JSONObject categoryJSON=(JSONObject) categoryList.get(j);
					Category category=new Category(categoryJSON.getString("name"));
					JSONArray TransList=getTransactionsDB(user,budget,category); 
					categoryJSON.put("transactionList", TransList);
				}
				budgetJSON.put("categoryList",categoryList);
			}
			for(int i=0;i<budgetList.length();++i){
				JSONObject budgetJSON=budgetList.getJSONObject(i);
				JSONArray categoryList=budgetJSON.getJSONArray("categoryList");
				int tranSpent=0;
				for(int j=0;j<categoryList.length();j++){
					JSONObject categoryJSON=(JSONObject) categoryList.get(j);
					JSONArray TransList=categoryJSON.getJSONArray("transactionList");
					double cateSpent=0;
					for(int k=0;k<TransList.length();k++){
						cateSpent+=TransList.getJSONObject(k).getDouble("amount");
					}
					categoryJSON.remove("categorySpent");
					categoryJSON.put("categorySpent",cateSpent);
					tranSpent+=cateSpent;
				}
				budgetJSON.remove("budgetSpent");
				budgetJSON.put("budgetSpent", tranSpent);
				
			}
			JSONObject info = new JSONObject();
			info.put("budgetLsit", budgetList);
			returnMessage.put("function", "returnEverything");
			returnMessage.put("information", info);
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("get Everything error");
		}catch (JSONException e) {
			System.out.println(e.getMessage());
			System.out.println("get Everything JSON error");
		}
		return returnMessage;
	}

	public JSONObject getBudgetList(User user) {
		JSONObject returnMessage = new JSONObject();
		try{
			JSONObject information = new JSONObject();
			JSONArray jsonArray=getBudgetListDB(user);
			information.put("budgetList", jsonArray);
			returnMessage.put("function", "returnBudgetList");
			returnMessage.put("status", "success");
			returnMessage.put("information", information);		
		}catch(SQLException e){
			System.out.println("getBudgetList error");
		}catch(JSONException e){
			System.out.println("getBudgetList error");
		}
		return returnMessage;	
	}
	
	
	public JSONObject editBudget(Budget toEdit, Budget original){
		JSONObject message = new JSONObject();
		System.out.println("into the editBudget");
		try{
			message.put("function", "editBudget");
			if(checkBudgetExist(toEdit)){
				message.put("status", "fail");
				message.put("detail", "duplicated budget name");
				System.out.println("check existed ");
			}
			else{
				System.out.println("before edit budget db");
				editBudgetDB(toEdit,original);
				message.put("status", "success");
				System.out.println("edit budget finish");
			}
		}catch (JSONException e) {
			System.out.println(e.getMessage());
			System.out.println("edit budget error with JSON");
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("edit budget with SQL");
		}
		return message;
	}
	public JSONObject createBudget(Budget toAdd){	
		try{
			JSONObject message = new JSONObject();
			message.put("function", "createBudget");// we need give more info to this message	
			if(checkBudgetExist(toAdd)){
				message.put("status", "fail");
				message.put("detail", "budget with same name already exist");
			}
			else{
				addBudgetDB(toAdd);
				CateDao.addCategory(toAdd);
				System.out.println("return success");
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
	
	private Boolean checkBudgetExist(Budget toAdd) throws SQLException{
		Connection conn=getDBConnection();
		BudgetFindUserID(toAdd);
		PreparedStatement st = conn.prepareStatement("SELECT * FROM SanityDB.Budget WHERE Budget_name=? "
				+ "AND User_id=?");// there cannot be two same name budget
		st.setString( 1, toAdd.budgetName);
		st.setInt(2, toAdd.userId);
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
			System.out.println("check budget exist error(add budget)");
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
	private void addBudgetDB(Budget toAdd) throws SQLException{
		System.out.println("add user");
		Connection conn=getDBConnection();
		BudgetFindUserID(toAdd);
		PreparedStatement st =  
				conn.prepareStatement("INSERT INTO SanityDB.Budget (Budget_name, "
						+ "User_id,Budget_period,Start_date,Budget_total,Budget_spent,Frequency, Threshold) VALUE(?,?,?,?,?,?,?,?)");
		st.setString( 1, toAdd.budgetName);
		st.setInt( 2, toAdd.userId);
		st.setInt(3, toAdd.period);
		st.setDate(4, java.sql.Date.valueOf(toAdd.date));
		st.setDouble(5, toAdd.budgetTotal);
		st.setDouble(6, 0);
		st.setInt(7, toAdd.frequency);
		st.setInt(8, toAdd.threshold);
		try{
			 st.executeUpdate();		
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("insert error(add budget)");
		}finally{
			if (conn != null) {
				conn.close();
			}
			if (st != null) {
				st.close();
			}
		}
	}
	private Boolean editBudgetDB(Budget toEdit, Budget original){		
		try{
			Connection conn=getDBConnection();
			// find the original budget and then update;
			BudgetFindUserID(toEdit);
			original.userId=toEdit.userId;
			BudgetFindBudgetID(original);
			toEdit.budgetId=original.budgetId;
		//	toEdit.userId=toEdit.userId;
			PreparedStatement updateStatement = conn.prepareStatement("UPDATE SanityDB.Budget SET Budget_name=?"
					+ ",Budget_period=?,"
					+ "Frequency=?,Threshold=? WHERE Budget_id=? AND User_id=?");
			updateStatement.setString(1, toEdit.budgetName);
			updateStatement.setInt(2, toEdit.period);
			//updateStatement.setDate(3, java.sql.Date.valueOf(toEdit.date));
			//updateStatement.setDouble(4, toEdit.budgetTotal);
			updateStatement.setInt(3, toEdit.frequency);
			updateStatement.setInt(4, toEdit.threshold);
			updateStatement.setInt(5, toEdit.budgetId);
			updateStatement.setInt(6, toEdit.userId);
			updateStatement.executeUpdate();
			return true;
		}catch(SQLException e){
			System.out.println("editBudgetDB Error");
		}
		return false;
	}
}
