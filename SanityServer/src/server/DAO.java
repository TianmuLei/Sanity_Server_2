package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	
	protected void CategoryFindBudgetID(Budget budget) throws SQLException{
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
}
