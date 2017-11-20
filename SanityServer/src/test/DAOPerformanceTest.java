package test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.DAO;
import server.DateCal;
import server.Transaction;
import server.TransactionDAO;
import server.User;

public class DAOPerformanceTest {
	TransactionDAO transactionDAO = new TransactionDAO();
	DAO tests = new DAO();
	Connection conn;
	@Before
	public void setUp() throws Exception {
		
		DAO.testing = 0;
		conn =transactionDAO.getDBConnection();
		String today =DateCal.today();
		String description = "this is a testing transaction";
		Transaction toAdd = new Transaction("yang@usc.edu", description, "qqq","testing",today,3.3);
		
		for(int i = 0 ; i < 1000; i++){
			transactionDAO.createTransaction(toAdd);
		}
		System.out.println(1111);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetEverythingOld() {
		long startTime = System.currentTimeMillis();
		User toadd = new User("yang", "yang@usc.edu", "123", "456");
		for(int i = 0; i < 100; i ++ ){
			tests.getEverythingOld(toadd, 0);
		}
		long endTime = System.currentTimeMillis();
		System.out.println(1111);
		System.out.println("First Time: "+ (endTime - startTime));
		startTime = System.currentTimeMillis();
		
		for(int i = 0; i < 100; i ++ ){
			tests.fetchAllData("chen716@usc.edu", 0);
		}
		endTime = System.currentTimeMillis();
		System.out.println("Second Time: "+(endTime - startTime));
		fail("Not yet implemented");
	}

}
