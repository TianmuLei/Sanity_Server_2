package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

public class TransactionDAO extends DAO{
	public JSONObject createTransaction(Transaction toAdd){	
		try{
			JSONObject message = new JSONObject();
			message.put("function", "createBudget");// we need give more info to this message	
			if(checkTransactionExist(toAdd)){
				message.put("status", "fail");
			}
			else{
				addTransactionDB(toAdd);
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
			
		}catch(SQLException e){
			
		}
		return false;
	}
	

	
	private void addTransactionDB(Transaction toAdd){
		
	}
}
