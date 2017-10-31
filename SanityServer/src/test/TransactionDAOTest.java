package test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.CategoryDAO;
import server.DAO;
import server.DateCal;
import server.Transaction;
import server.TransactionDAO;

public class TransactionDAOTest {
	TransactionDAO transactionDAO = new TransactionDAO();
	Connection conn;
	
	
	@Before
	public void setUp() throws Exception {
		DAO.testing = 0;
		conn =transactionDAO.getDBConnection();
	}
	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testAddTransaction() {
		String today =DateCal.today();
		String description = "this is a testing transaction";
		Transaction toAdd = new Transaction("yang@usc.edu", description, "testing","testing",today,3.3);
		try{
			PreparedStatement statement = conn.prepareStatement("DELETE FROM SanityDB.Transaction WHERE "
					+ "Transaction_description=?");
			statement.setString(1, description);
			statement.executeUpdate();
			transactionDAO.createTransaction(toAdd);
			
			PreparedStatement getParameter = conn.prepareStatement("SELECT * FROM SanityDB.Sanity_transaction WHERE Email=? "
					+ "AND Budget_name=? AND Category_name=?");
			getParameter.setString(1, "yang@usc.edu");
			getParameter.setString(2, "testing");
			getParameter.setString(3, "testing");
			ResultSet resultSet=getParameter.executeQuery();
			assertEquals(true, resultSet.next());
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("testAddTransaction sql error");
		}
	}
	
	@Test
	public void testTransactionExist(){
		String today =DateCal.today();
		String description = "this is a testing transaction";
		Transaction toAdd = new Transaction("yang@usc.edu", description, "testing","testing",today,3.3);
		try{
			assertEquals(true, transactionDAO.checkTransactionExist(toAdd));
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
		
	}

}
