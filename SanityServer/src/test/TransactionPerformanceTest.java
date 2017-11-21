package test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.DAO;
import server.DateCal;
import server.Transaction;
import server.TransactionDAO;

public class TransactionPerformanceTest {
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
	public void testCheckTransactionExistNew() {
		String today =DateCal.today();
		String description = "this is a testing transaction";
		Transaction toAdd = new Transaction("yang@usc.edu", description, "qqq","testing",today,3.3);
		long startTime = System.currentTimeMillis();
		for(int i = 0 ; i < 100; i ++ ){
			try {
				transactionDAO.checkTransactionExist(toAdd);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("First Time: "+ (endTime - startTime));
		long time1 = endTime - startTime;
		startTime = System.currentTimeMillis();
		for(int i = 0 ; i < 100; i ++ ){
			try {
				transactionDAO.checkTransactionExistNew(toAdd);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		endTime = System.currentTimeMillis();
		System.out.println("First Time: "+ (endTime - startTime));
		long time2 = endTime - startTime;
		assertEquals(true, time1>time2);
	}

}
