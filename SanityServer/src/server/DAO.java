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
	protected Connection getDBConnection() {
		Connection dbConnection = null;
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
	
	protected Integer UserFindUserID(User user) throws SQLException{
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
	protected void BudgetFindUserID(Budget budget) throws SQLException{
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
				temp.put("date", rs.getDate("Start_date").toString());
				temp.put("budgetTotal", rs.getDouble("Budget_total"));
				temp.put("budgetSpent", rs.getDouble("Budget_spent"));
				temp.put("threshold",rs.getInt("Threshold"));
				temp.put("frequency", rs.getInt("Frequency"));
				temp.put("period", rs.getInt("Budget_period"));
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
				temp.put("categorySpent", rs.getDouble("Category_spent"));
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
	
	public JSONArray getTransactionsDB(User user, Budget budget, Category category){
		JSONArray transList= new JSONArray();
		try{
			category.userID =UserFindUserID(user);
			budget.userId=category.userID;
			BudgetFindBudgetID(budget);
			category.budgetID=budget.budgetId;
			CategoryFindCategoryID(category);
			Connection conn= getDBConnection();
			PreparedStatement getTransactions = conn.prepareStatement("SELECT * FROM SanityDB.Transaction "
					+ "WHERE User_id=? AND Budget_id=? AND Category_id=?");
			getTransactions.setInt(1, category.userID);
			getTransactions.setInt(2, category.budgetID);
			getTransactions.setInt(3, category.categoryID);
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
}
