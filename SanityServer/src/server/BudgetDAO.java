package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.IOP.RMICustomMaxStreamFormat;

public class BudgetDAO extends DAO{
	CategoryDAO CateDao;
	public BudgetDAO(){
		CateDao = new CategoryDAO();
	}
	public JSONObject getEverything(User user,Integer period){
		return fetchAllData(user.email,period);
	}
	
	public JSONObject sendBudgetSummary(String budgetName, String email){
		JSONObject returnMessage = new JSONObject();
		try{
			returnMessage.put("function", "requestSummary");
			JSONObject messageNeeded = fetchAllData(email, 1);
			messageNeeded = messageNeeded.getJSONObject("information");
			JSONArray array = messageNeeded.getJSONArray("budgetLsit");
			for(int i=0;i<array.length();++i){
				JSONObject temp = (JSONObject)array.get(i);
				if(temp.getString("name").equals(budgetName)){
					messageNeeded=temp;
				}
			}
			String userName = getUsername(email);
			BudgetEmailSender sender = new BudgetEmailSender(messageNeeded,email,userName);
			sender.start();
			returnMessage.put("status", "success");
		}catch (JSONException e) {
			System.out.println(e.getMessage());
			System.out.println("JSON error in sendBudgetSummary");
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
			Boolean allowEdit =true;
			message.put("function", "editBudget");
			System.out.println("new name"+ toEdit.budgetName);
			System.out.println("old name"+ original.budgetName);
			if(!toEdit.budgetName.equals(original.budgetName)){
				if(checkBudgetExist(toEdit)){
					message.put("status", "fail");
					message.put("detail", "duplicated budget name");
					System.out.println("check existed ");
					allowEdit=false;
				}
			}      
			if(allowEdit){
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
	public JSONObject deleteBudget(String email, String budgetName){
		JSONObject message = new JSONObject();
		try{
			message.put("function", "deleteBudget");
			deleteBudgetDB(email, budgetName);
			message.put("status", "success");
		}catch (JSONException e) {
			System.out.println(e.getMessage());
			System.out.println("JSON error in deleteBudget");
		}
		return message;
	}
	private void deleteBudgetDB(String email, String budgetName){
		Connection conn = getDBConnection();
		try{
			PreparedStatement getBudgetID = conn.prepareStatement("SELECT * FROM SanityDB.Sanity_budget WHERE Email=? AND Budget_name=?");
			getBudgetID.setString(1, email);
			getBudgetID.setString(2, budgetName);
			ResultSet resultSet =getBudgetID.executeQuery();
			Integer budgetID=-1;
			if(resultSet.next()){
				budgetID=resultSet.getInt("Budget_id");
			}
			if(resultSet!=null){
				resultSet.close();
			}
			if(getBudgetID!=null){
				getBudgetID.close();
			}
			deleteCategoryWithBudget(budgetID, conn);
			PreparedStatement deleteBudget = conn.prepareStatement("DELETE FROM SanityDB.Budget WHERE Budget_id=?");
			deleteBudget.setInt(1, budgetID);
			deleteBudget.executeUpdate();
			if(deleteBudget!=null){
				deleteBudget.close();
			}
			if(conn!=null){
				conn.close();
			}
		}catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("SQL error in deleteBudgetDB");
		}
		
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
			Integer userID =-1;
			Integer budgetID=-1;
			PreparedStatement findUserID = conn.prepareStatement("SELECT * FROM SanityDB.Sanity_budget WHERE Email=? AND Budget_name=?");
			findUserID.setString(1, toEdit.email);
			findUserID.setString(2, original.budgetName);
			ResultSet resultSet=findUserID.executeQuery();
			if(resultSet.next()){
				userID=resultSet.getInt("User_id");
				budgetID=resultSet.getInt("Budget_id");
			}
			if(resultSet!=null){
				resultSet.close();
			}
			if(findUserID!=null){
				findUserID.close();
			}
			PreparedStatement updateStatement = conn.prepareStatement("UPDATE SanityDB.Budget SET Budget_name=?"
					+ ",Budget_period=?,"
					+ "Frequency=?,Threshold=? WHERE Budget_id=? AND User_id=?");
			updateStatement.setString(1, toEdit.budgetName);
			updateStatement.setInt(2, toEdit.period);
			updateStatement.setInt(3, toEdit.frequency);
			updateStatement.setInt(4, toEdit.threshold);
			updateStatement.setInt(5, budgetID);
			updateStatement.setInt(6, userID);
			updateStatement.executeUpdate();
			if(updateStatement!=null){
				updateStatement.close();
			}
			if(conn!=null){
				conn.close();
			}
			return true;
		}catch(SQLException e){
			System.out.println("editBudgetDB Error");
		}
		return false;
	}
	
	public Boolean shareBudget(String email,String budgetName,String emailShare){
		
		try{
			User user=new User(emailShare);
			Integer idShare=UserFindUserID(user);
			if(idShare==-1){
				System.out.println("cannot find user");
				return false;
			}
			
			Connection conn = getDBConnection();
			PreparedStatement statement=conn.prepareStatement("SELECT * FROM SanityDB.Sanity_budget WHERE Email=? AND Budget_name=?");
			statement.setString(1, email);
			statement.setString(2, budgetName);
			Integer oldId;
			
			ResultSet rs = statement.executeQuery();
			if(rs.next()){
				//copy budget
				statement =  
						conn.prepareStatement("INSERT INTO SanityDB.Budget (Budget_name, "
								+ "User_id,Budget_period,Start_date,Budget_total,Budget_spent,Frequency, Threshold) VALUE(?,?,?,?,?,?,?,?)");
				statement.setString( 1, rs.getString("Budget_name"));
				statement.setInt( 2, idShare);
				statement.setInt(3, rs.getInt("Budget_period"));
				statement.setDate(4, java.sql.Date.valueOf( rs.getDate("Start_date").toString()));
				statement.setDouble(5, rs.getDouble("Budget_total"));
				statement.setDouble(6, 0);
				statement.setInt(7,  rs.getInt("Frequency"));
				statement.setInt(8, rs.getInt("Threshold"));
				oldId=rs.getInt("Budget_id");
				
				statement.executeUpdate();	
				
			}
			else{
				System.out.println("cannot find budget");
				return false;
			}
			
			//find new budget id
			Integer newId;
			statement=conn.prepareStatement("SELECT * FROM SanityDB.Sanity_budget WHERE Email=? AND Budget_name=?");
			statement.setString(1, emailShare);
			statement.setString(2, budgetName);
			rs = statement.executeQuery();
			if(rs.next()){
				newId=rs.getInt("Budget_id");
			}
			else{
				System.out.println("cannot find new id");
				return false;
			}
			//copy category
			statement=conn.prepareStatement("SELECT * FROM SanityDB.Sanity_category WHERE Budget_name=? AND Email=?");
			statement.setString(1, budgetName);
			statement.setString(2, email);
			rs=statement.executeQuery();
			while(rs.next()){
				statement = conn.prepareStatement("INSERT INTO SanityDB.Category(User_id, Category_name, Budget_id,"
						+ "Category_total, Category_spent) VALUE(?,?,?,?,?)");
				statement.setInt(1, idShare);
				statement.setString(2,rs.getString("Category_name"));
				statement.setInt(3, newId);
				statement.setInt(4, rs.getInt("Category_total"));
				statement.setInt(5,0);
				
				statement.executeUpdate();
			}
			//add in share
			statement = conn.prepareStatement("SELECT * FROM  SanityDB.Share");
			rs=statement.executeQuery();
			Boolean find=false;
			String oldIDString=oldId.toString();
			while(rs.next()){
				String group=rs.getString("Share_budget");
				String[] groupSplit=group.split(",");
				for(int i=0;i<groupSplit.length;i++){
					if(groupSplit[i].equals(oldIDString)){
						find=true;
						String newgroup=group+","+newId.toString();
						statement = conn.prepareStatement("UPDATE  SanityDB.Share SET Share_budget=? WHERE Share_id=?");
						statement.setString(1, newgroup);
						statement.setInt(2, rs.getInt("Share_id"));
						statement.executeUpdate();
					}
				}
				if(find==false){
					String insert=oldIDString+","+newId.toString();
					statement = conn.prepareStatement("INSERT INTO SanityDB.Share(Share_budget) VALUE(?)");
					statement.setString(1, insert);
					statement.executeUpdate();
				}		
			}
			
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("sharebudgeterrror");
			
		}
		
		
		return false;
		
	}
}
