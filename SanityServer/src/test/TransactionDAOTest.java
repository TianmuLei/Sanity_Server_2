package test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.CategoryDAO;
import server.DAO;
import server.DateCal;
import server.Transaction;

public class TransactionDAOTest {
	CategoryDAO categoryDAO = new CategoryDAO();
	Connection conn;
	
	
	@Before
	public void setUp() throws Exception {
		DAO.testing = 0;
		conn =categoryDAO.getDBConnection();
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
			PreparedStatement getParameter = conn.prepareStatement("SELECT * FROM SanityDB.Sanity_transaction WHERE Email=? AND Budget_name=? AND Category_name=?");
			PreparedStatement addTransaction =conn.prepareStatement("INSERT INTO SanityDB.Transaction"
					+ " () VALUE()");
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("testAddTransaction sql error");
		}
	}

}
