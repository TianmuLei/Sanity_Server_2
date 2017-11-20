package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DAO {
	public static int testing =1;
	public Connection getDBConnection() {
		Connection dbConnection = null;
		if(testing != 1){
			try {
				return dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/SanityDB?user=root&password=developer&useSSL=false");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {
			dbConnection= DriverManager.getConnection("jdbc:mysql://127.0.0.1/SanityDB?user=root&password=chenyang&useSSL=false");		
			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return dbConnection;
	}
	
	public String findShare(Integer toFindID){
		try{
			Connection conn = getDBConnection();
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM  SanityDB.Share");
			ResultSet rs=statement.executeQuery();
			while(rs.next()){
				String group=rs.getString("Share_budget");
				String[] groupSplit=group.split(",");
				for(int i=0;i<groupSplit.length;i++){
					if(groupSplit[i].equals(toFindID.toString())){
						return group;
					}
				}		
			}
			
		}
			
		catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		
		
		return "-1";
	}
	
	public Integer UserFindUserID(User user) throws SQLException{
		Integer toReturn=-1;
		Connection conn=getDBConnection();
		PreparedStatement st = conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=?");
		st.setString( 1, user.email);
		try{
			ResultSet rs = st.executeQuery();
			if(rs.next()){
				toReturn=rs.getInt("User_id");
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}finally{
			if (conn != null) {
				conn.close();
			}
			if (st != null) {
				st.close();
			}
		}		
		return toReturn;
	}
	public void BudgetFindUserID(Budget budget) throws SQLException{
		Connection conn = getDBConnection();
		PreparedStatement userStatement = conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=?");
		userStatement.setString(1, budget.email);
		try{
			ResultSet rs= userStatement.executeQuery();
			rs.next();
			budget.userId=rs.getInt("User_id");
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("find user error(add budget)");
		}finally{
			if (userStatement != null) {
				userStatement.close();
			}
		}	
	}
	
	protected void BudgetFindBudgetID(Budget budget) throws SQLException{
		Connection conn = getDBConnection();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM SanityDB.Budget WHERE Budget_name=?"
				+ "AND User_id=?");
		statement.setString(1, budget.budgetName);
		statement.setInt(2, budget.userId);
		try{
			ResultSet rs = statement.executeQuery();
			rs.next();
			budget.budgetId=rs.getInt("Budget_id");
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("find budget_id error(add category)");
		}finally{
			if (statement != null) {
				statement.close();
			}
			if(conn!=null){
				conn.close();
			}
		}
	}
	protected void TransactionFindUserID(Transaction tran) throws SQLException{
		Connection conn = getDBConnection();
		PreparedStatement findUserId= conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=?");
		findUserId.setString(1, tran.email);
		try{
			ResultSet rs = findUserId.executeQuery();
			rs.next();
			tran.userID=rs.getInt("User_id");
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("find user id error(add transaction) ");
		}finally{
			if(conn!=null){
				conn.close();
			}
			if (findUserId != null) {
				findUserId.close();
			}
		}
	}
	protected void TransactionFindBudgetID(Transaction tran) throws SQLException{
		Connection conn = getDBConnection();
		PreparedStatement findBudgetId= conn.prepareStatement("SELECT * FROM SanityDB.Budget WHERE Budget_name=? AND "
				+ "User_id=?");
		findBudgetId.setString(1, tran.budget);
		findBudgetId.setInt(2, tran.userID);// need to add the user
		try{
			ResultSet rs = findBudgetId.executeQuery();
			rs.next();
			tran.budgetID=rs.getInt("Budget_id");
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("find budget id error(add transaction) ");
		}finally{
			if (findBudgetId != null) {
				findBudgetId.close();
			}
			if(conn!=null){
				conn.close();
			}
		}
	}
	protected void CategoryFindCategoryID(Category cate) throws SQLException{
		try{
			Connection conn=getDBConnection();
			PreparedStatement getCategoryID = conn.prepareStatement("SELECT * FROM SanityDB.Category WHERE "
					+ "User_id=? AND Budget_id=? AND Category_name=?");
			getCategoryID.setInt(1, cate.userID);
			getCategoryID.setInt(2, cate.budgetID);
			getCategoryID.setString(3, cate.categoryName);
			ResultSet rs = getCategoryID.executeQuery();
			rs.next();
			cate.categoryID=rs.getInt("Category_id");
			if(getCategoryID!=null){
				getCategoryID.close();
			}
			if(conn!=null){
				conn.close();
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("CategoryFindCategory Error");
		}
	}
	
	protected void TransactionFindCategoryID(Transaction tran) throws SQLException{
		Connection conn = getDBConnection();
		PreparedStatement findCategoryID = conn.prepareStatement("SELECT * FROM SanityDB.Category"
				+ " WHERE Budget_id=? AND User_id=? AND Category_name=?");
		findCategoryID.setInt(1, tran.budgetID);
		findCategoryID.setInt(2, tran.userID);
		findCategoryID.setString(3, tran.category);
		try{
			ResultSet rs = findCategoryID.executeQuery();
			rs.next();
			tran.categoryID=rs.getInt("Category_id");
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("find category id error(add transaction) ");
		}finally{
			if (findCategoryID != null) {
				findCategoryID.close();
			}
			if(conn!=null){
				conn.close();
			}
		}
	}
	protected JSONArray getBudgetListDB(User user) throws SQLException{
		Connection conn=getDBConnection();
		PreparedStatement findAllBudget= conn.prepareStatement("SELECT * FROM SanityDB.Budget WHERE User_id=?");
		Integer userID=-1;
		JSONArray Jarray= new JSONArray();
		try{
			userID=UserFindUserID(user);
			findAllBudget.setInt(1, userID);
			ResultSet rs =findAllBudget.executeQuery();
			while(rs.next()){
				JSONObject temp = new JSONObject();
				temp.put("name", rs.getString("Budget_name"));
				String start=rs.getDate("Start_date").toString();
				temp.put("date", start);
				temp.put("budgetTotal", rs.getDouble("Budget_total"));
				temp.put("budgetSpent",0);
				temp.put("threshold",rs.getInt("Threshold"));
				temp.put("frequency", rs.getInt("Frequency"));
				Integer period=rs.getInt("Budget_period");
				temp.put("period",period );
				
				String startDate=DateCal.calculateCurrentStart(start,period, 0);
				String endDate;
				endDate=DateCal.getEndDate(startDate, period);
				long remain=DateCal.getRemian(endDate);
				temp.put("remain", remain);
				
				Jarray.put(temp);
			}
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("getBudgetListDB error");
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("getBudgetListDB error");
		}
		return Jarray;
	}
	
	protected JSONArray getCategoriesListDB(User user, Budget budget){
		JSONArray Jarray=new JSONArray();
		PreparedStatement findAllCategory;
		try{
			Integer user_id=UserFindUserID(user);
			budget.userId=user_id;
			BudgetFindBudgetID(budget);
			Integer budgetID=budget.budgetId;
			Connection conn=getDBConnection();
			findAllCategory= conn.prepareStatement("SELECT * FROM SanityDB.Category WHERE User_id=? AND Budget_id=?");
			findAllCategory.setInt(1, user_id);
			findAllCategory.setInt(2, budgetID);
			ResultSet rs =findAllCategory.executeQuery();
			while(rs.next()){
				JSONObject temp = new JSONObject();
				temp.put("name", rs.getString("Category_name"));
				temp.put("limit", rs.getDouble("Category_total"));
				temp.put("categorySpent",0);
				temp.put("budgetName", budget.budgetName);
				temp.put("requestPeriod", budget.requestPeriod);
				Jarray.put(temp);
			}
			if (findAllCategory != null) {
				findAllCategory.close();
			}
			if(conn!=null){
				conn.close();
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("Error in getCategory");
		}catch (JSONException e) {
			System.out.println(e.getMessage());
			System.out.println("JSON Error in getCategory");
		}		
		return Jarray;
	}
	
	protected JSONArray getTransactionsDB(User user, Budget budget, Category category){
		JSONArray transList= new JSONArray();
		try{
			category.userID =UserFindUserID(user);
			budget.userId=category.userID;
			BudgetFindBudgetID(budget);
			category.budgetID=budget.budgetId;
			CategoryFindCategoryID(category);
			Connection conn= getDBConnection();
			PreparedStatement getTransactions = conn.prepareStatement("SELECT * FROM SanityDB.Transaction "
					+ "WHERE User_id=? AND Budget_id=? AND Category_id=? AND Transaction_date between ? and ?");
			
			String startDate=DateCal.calculateCurrentStart(budget.date, budget.period, budget.requestPeriod);
			String endDate;
			if(budget.requestPeriod==0){
				endDate=DateCal.today();
			}
			else{
				endDate=DateCal.getEndDate(startDate, budget.period);
			}
			getTransactions.setInt(1, category.userID);
			getTransactions.setInt(2, category.budgetID);
			getTransactions.setInt(3, category.categoryID);
			getTransactions.setDate(4,java.sql.Date.valueOf(startDate));
			getTransactions.setDate(5,java.sql.Date.valueOf(endDate));
			ResultSet rs=getTransactions.executeQuery();
			//JSONObject generalInfo = new JSONObject();
			
			
			while(rs.next()){
				JSONObject temp = new JSONObject();
				temp.put("description", rs.getString("Transaction_description"));
				temp.put("amount", rs.getDouble("Transaction_amount"));
				temp.put("date", rs.getDate("Transaction_date").toString());
				temp.put("budgetName", budget.budgetName);
				temp.put("categoryName", category.categoryName);
				transList.put(temp);
			}
		}catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("getTrasactions error");
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("getTransactions error");
		}
		return transList;
	}
	

	
	public JSONObject fetchAllData(String email,Integer period){
		JSONObject returnMessage = new JSONObject();
		try{
			Connection conn = getDBConnection();
			PreparedStatement statement=conn.prepareStatement("SELECT * FROM SanityDB.Sanity_budget WHERE Email=?");
			statement.setString(1, email);
			JSONArray budgetList = new JSONArray();
			ResultSet rs=statement.executeQuery();
			while(rs.next()){
				JSONObject temp = new JSONObject();
				temp.put("name", rs.getString("Budget_name"));
				String start = rs.getDate("Start_date").toString();
				temp.put("date", start);
				temp.put("budgetTotal", rs.getDouble("Budget_total"));
				temp.put("budgetSpent", 0);
				temp.put("threshold", rs.getInt("Threshold"));
				temp.put("frequency", rs.getInt("Frequency"));
				temp.put("period", rs.getInt("Budget_period"));
				
				String startDate = DateCal.calculateCurrentStart(start, temp.getInt("period"),0);
				String endDate = DateCal.getEndDate(startDate, temp.getInt("period"));
				
				long remain =DateCal.getRemian(endDate);
				
				if(period==0){
					endDate=DateCal.today();
				}
				
				
				temp.put("remain", remain);
				temp.put("startDate", startDate);
				temp.put("endDate", endDate);
				
				budgetList.put(temp);
			}
			if(rs!=null){
				rs.close();
			}
			if(statement!=null){
				statement.close();
			}
			for(int j=0;j<budgetList.length();j++){
				JSONObject budgetJSON=(JSONObject) budgetList.get(j);
				JSONArray categoryList=new JSONArray();
				PreparedStatement st=conn.prepareStatement("SELECT * FROM SanityDB.Sanity_category WHERE Budget_name=? AND Email=?");
				st.setString(1, budgetJSON.getString("name"));
				st.setString(2, email);
				ResultSet categoryResult=st.executeQuery();
				while(categoryResult.next()){
					JSONObject temp = new JSONObject();
					temp.put("name", categoryResult.getString("Category_name"));
					temp.put("limit", categoryResult.getDouble("Category_total"));
					temp.put("categorySpent",0);
					temp.put("budgetName", budgetJSON.getString("name"));
					temp.put("requestPeriod", period);
					categoryList.put(temp);
				}
				budgetJSON.put("categoryList",categoryList);
				if(categoryResult!=null){
					categoryResult.close();
				}
				if(st!=null){
					st.close();
				}
			}
			
			for(int i=0;i<budgetList.length();i++){
				JSONObject budget = (JSONObject)budgetList.get(i);
				JSONArray categoryList = budget.getJSONArray("categoryList");
				for(int j=0; j<categoryList.length();++j){
					JSONObject category=(JSONObject)categoryList.get(j);
					PreparedStatement getTransaction = conn.prepareStatement("SELECT * FROM SanityDB.Sanity_transaction WHERE Budget_name=? AND"
							+ " Category_name=? AND Email=? AND Transaction_date >= ? AND Transaction_date <=?");
					getTransaction.setString(1, category.getString("budgetName"));
					getTransaction.setString(2, category.getString("name"));
					getTransaction.setString(3, email);
					String date = budget.getString("date");
					Integer budgetPeriod = budget.getInt("period");
					String startDate=DateCal.calculateCurrentStart(date, budgetPeriod, period);
					String endDate =DateCal.getEndDate(startDate, budgetPeriod);
					getTransaction.setDate(4, java.sql.Date.valueOf(startDate));
					getTransaction.setDate(5,java.sql.Date.valueOf(endDate));
					ResultSet transactions=getTransaction.executeQuery();
					JSONArray transactionList = new JSONArray();
					while(transactions.next()){
						JSONObject temp = new JSONObject();
						temp.put("description", transactions.getString("Transaction_description"));
						temp.put("amount", transactions.getDouble("Transaction_amount"));
						temp.put("date", transactions.getDate("Transaction_date").toString());
						temp.put("budgetName", transactions.getString("Budget_name"));
						temp.put("categoryName", transactions.getString("Category_name"));
						transactionList.put(temp);
					}
					category.put("transactionList", transactionList);
					if(transactions!=null){
						transactions.close();
					}
					if(getTransaction!=null){
						getTransaction.close();
					}
				}
			}
			
			for(int i=0;i<budgetList.length();++i){
				JSONObject budgetJSON=budgetList.getJSONObject(i);
				JSONArray categoryList=budgetJSON.getJSONArray("categoryList");
				double tranSpent=0;
				double budgetTotal =0;
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
					budgetTotal += categoryJSON.getDouble("limit");
				}
				budgetJSON.remove("budgetSpent");
				budgetJSON.put("budgetSpent", tranSpent);
				budgetJSON.remove("budgetTotal");
				budgetJSON.put("budgetTotal", budgetTotal);	
			}
			JSONObject info = new JSONObject();
			info.put("budgetLsit", budgetList);
			returnMessage.put("function", "returnEverything");
			returnMessage.put("information", info);
			if(conn!=null){
				conn.close();
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("fetch all data SQL error");
		}catch(JSONException e){
			System.out.println(e.getMessage());
			System.out.println("fetch all data JSON error");
		}
		return returnMessage;
	}
	protected void deleteCategoryDB(String email, String budgetName, String categoryName){
		try{
			Connection conn= getDBConnection();
			PreparedStatement getCategoryID=conn.prepareStatement("SELECT * FROM SanityDB.Sanity_category WHERE Email=? AND"
					+ " Budget_name=? AND Category_name=?");
			getCategoryID.setString(1, email);
			getCategoryID.setString(2, budgetName);
			getCategoryID.setString(3, categoryName);
			ResultSet resultSet=getCategoryID.executeQuery();
			Integer categoryID=-1;
			if(resultSet.next()){
				categoryID = resultSet.getInt("Category_id");
			}
			if(resultSet!=null){
				resultSet.close();
			}
			if(getCategoryID!=null){
				getCategoryID.close();
			}
			deleteTransactionWithCategory(categoryID,conn);
			PreparedStatement deleteCategory = conn.prepareStatement("DELETE FROM SanityDB.Category WHERE Category_id=?");
			deleteCategory.setInt(1, categoryID);
			deleteCategory.executeUpdate();
			if(deleteCategory!=null){
				deleteCategory.close();
			}
			if(conn!=null){
				conn.close();
			}
		}catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("delete category SQL error");
		}
	}
	protected void deleteTransactionWithCategory(Integer categoryID,Connection conn){
		try{
			PreparedStatement deleteTransaction = conn.prepareStatement("DELETE FROM SanityDB.Transaction WHERE Category_id=?");
			deleteTransaction.setInt(1, categoryID);
			deleteTransaction.executeUpdate();
			if(deleteTransaction!=null){
				deleteTransaction.close();
			}
			
		}catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("delete Transaction with category error");
		}
	}
	protected void deleteCategoryWithBudget(Integer budgetID,Connection conn){
		deleteTransacitonWithBudget(budgetID, conn);
		try{
			PreparedStatement deleteCategory = conn.prepareStatement("DELETE FROM SanityDB.Category WHERE Budget_id=?");
			deleteCategory.setInt(1, budgetID);
			deleteCategory.executeUpdate();
			if(deleteCategory!=null){
				deleteCategory.close();
			}
		}catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("deleteCategoryWithBudget SQL error");
		}
	}
	protected void deleteTransacitonWithBudget(Integer budgetID,Connection conn){
		try{
			PreparedStatement deleteTransaction= conn.prepareStatement("DELETE FROM SanityDB.Transaction WHERE Budget_id=?");
			deleteTransaction.setInt(1, budgetID);
			deleteTransaction.executeUpdate();
			if(deleteTransaction!=null){
				deleteTransaction.close();
			}
		}catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("deleteTransactionWithBudget SQL error");
		}
	}
	protected String getUsername(String email){
		String toReturn ="";
		try{
			Connection conn=getDBConnection();
			PreparedStatement statement= conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=?");
			statement.setString(1, email);
			ResultSet rs= statement.executeQuery();
			rs.next();
			toReturn=rs.getString("Username");
			if(statement!=null){
				statement.close();
			}
			if(conn!=null){
				conn.close();
			}
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("getUsername Error with email");
		}
		return toReturn;
	}
	public JSONObject getEverythingOld(User user,Integer period){
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
}
