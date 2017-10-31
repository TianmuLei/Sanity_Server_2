package test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.Budget;
import server.DAO;
import server.User;

public class DAOTest {
	//run after the UserDAO test
	DAO tests = new DAO();
	Connection conn;
	@Before
	public void setUp() throws Exception {
		DAO.testing = 0;
		conn =tests.getDBConnection();
	}
	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void UserFindUserIDTest() {
		try{
			Integer userID =tests.UserFindUserID(new User("yang@usc.edu"));
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM SanityDB.sanity_budget WHERE Email='yang@usc.edu'");
			ResultSet rs=statement.executeQuery();
			Integer userIDtest =-1;
			if(rs.next()){
				userIDtest=rs.getInt("User_id");
			}
			assertEquals(userIDtest, userID);
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	public void BudgetFindUserIDTest() {
		try{
			Budget toTest = new Budget("testing");
			tests.BudgetFindUserID(toTest);
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM SanityDB.sanity_budget WHERE Budget_name='testing'");
			ResultSet rs=statement.executeQuery();
			Integer userIDtest =-1;
			if(rs.next()){
				userIDtest=rs.getInt("User_id");
			}
			assertEquals(userIDtest, toTest.userId);
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
	}
}
