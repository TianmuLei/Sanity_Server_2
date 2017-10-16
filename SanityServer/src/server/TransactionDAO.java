package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.UpdatableResultSet;

public class TransactionDAO extends DAO{
	public JSONObject createTransaction(Transaction toAdd){	
		try{
			JSONObject message = new JSONObject();
			message.put("function", "createBudget");// we need give more info to this message	
			if(checkTransactionExist(toAdd)){
				message.put("status", "fail");
				message.put("detail", "same transaction alrealy exist");
			}
			else{
				addTransactionDB(toAdd);
				updateDB(toAdd);
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
	
	private Boolean checkTransactionExist(Transaction toAdd) throws SQLException{
		Connection conn= getDBConnection();
		TransactionFindUserID(toAdd);
		TransactionFindBudgetID(toAdd);
		TransactionFindCategoryID(toAdd);
		PreparedStatement findTransaction = conn.prepareStatement("SELECT * FROM SanityDB.Transaction WHERE"
				+ "Transaction_description=? AND User_id=? AND Transaction_amount=? AND Transaction_date=?"
				+ "AND Budget_id=? AND Category_id=?");
		findTransaction.setString(1, toAdd.description);
		findTransaction.setInt(2, toAdd.userID);
		findTransaction.setDouble(3, toAdd.amount);
		findTransaction.setDate(4,java.sql.Date.valueOf(toAdd.date));
		findTransaction.setInt(5, toAdd.budgetID);
		findTransaction.setInt(6, toAdd.categoryID);
		try{
			ResultSet rs = findTransaction.executeQuery();
			if(rs.next()){
				return false;
			}
			else{
				return true;
			}
		}catch(SQLException e){
			System.out.println("check transaction error ");
		}finally{
			if(findTransaction!=null){
				findTransaction.close();
			}
			if(conn!=null){
				conn.close();
			}
		}
		return false;
	}	
	private void addTransactionDB(Transaction toAdd) throws SQLException{
		Connection conn= getDBConnection();
		PreparedStatement insert = conn.prepareStatement("INSERT INTO SanityDB.Transaction (Transaction_description, "
				+ "User_id, Transaction_amount, Transaction_date,Budget_id,Category_id) VALUE (?,?,?,?,?,?)");
		insert.setString(1, toAdd.description);
		insert.setInt(2, toAdd.userID);
		insert.setDouble(3, toAdd.amount);
		insert.setDate(4, java.sql.Date.valueOf(toAdd.date));
		insert.setInt(5, toAdd.budgetID);
		insert.setInt(6, toAdd.categoryID);
		try{
			insert.executeUpdate();
		}catch(SQLException e){
			System.out.println("check transaction error ");
		}finally{
			if(insert!=null){
				insert.close();
			}
			if(conn!=null){
				conn.close();
			}
		}
	}
	private void updateDB(Transaction toAdd) throws SQLException{
		Connection conn= getDBConnection();
		Double budgetspent=0.0;
		Double categoryspent=0.0;
		PreparedStatement getbudget = conn.prepareStatement("SELECT * FROM SanityDB.Budget WHERE Budget_id=?");
		PreparedStatement getcategory = conn.prepareStatement("SELECT* FROM SanityDB.Category WHERE Category_id=?");
		getbudget.setInt(1, toAdd.budgetID);
		getcategory.setInt(1, toAdd.categoryID);
		try{
			ResultSet budget =getbudget.executeQuery();
			ResultSet cate = getcategory.executeQuery();
			budget.next();
			budgetspent=budget.getDouble("Budget_spent");
			cate.next();
			categoryspent=cate.getDouble("Category_spent");
		}catch(SQLException e){
			System.out.println("check transaction error ");
		}finally{
			if(getbudget!=null){
				getbudget.close();
			}
			if(getcategory!=null){
				getcategory.close();
			}
		}
		budgetspent+= toAdd.amount;
		categoryspent+=toAdd.amount;
		PreparedStatement updateBudget= conn.prepareStatement("UPDATE SanityDB.Budget SET Budget_spent=?"
				+ "WHERE Budget_id =? ");
		PreparedStatement updateCategory= conn.prepareStatement("UPDATE SanityDB.Category SET Category_spent=?"
				+ "WHERE Category_id =? ");
		updateBudget.setDouble(1, budgetspent);
		updateBudget.setDouble(2, toAdd.budgetID);
		updateCategory.setDouble(1, categoryspent);
		updateCategory.setDouble(2, toAdd.categoryID);
	}
}
