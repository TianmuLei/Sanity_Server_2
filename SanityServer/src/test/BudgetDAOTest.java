package test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.*;
public class BudgetDAOTest {
	BudgetDAO test = new BudgetDAO();
	Connection conn;
	@Before
	public void setUp() throws Exception {
		conn = test.getDBConnection();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testCreateBudget() {
		ArrayList<Category> toadd = new ArrayList<Category>();
		toadd.add(new Category("qqq", 100 ));
		Budget Btoadd = new Budget("testing","yang@usc.edu", DateCal.today(), 10, 1000, 3, 75,toadd );
		try{
			PreparedStatement st = conn.prepareStatement("DELETE FROM SanityDB.Budget where Email='yang@usc.edu'");
			st.executeUpdate();
			st = conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=? ");
			st.setString( 1, "yang@usc.edu");
			ResultSet rs = st.executeQuery();
			System.out.println(rs.next());
			if(rs.next()){
				fail("Email already exist");
			}
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
			}
		
		JSONObject getret =  tests.Register(toadd);
		try{
			PreparedStatement st = conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=? AND Password1=? AND Password2=?");
			st.setString( 1, "yang@usc.edu");
			st.setString( 2, "123");
			st.setString( 3, "456");
			ResultSet rs = st.executeQuery();
			
			assertEquals(true, rs.next());
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
			}
		
		 try {
			if(conn != null && !conn.isClosed()){
			     conn.close();
			 }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

	@Test
	public void testGetBudgetList() {
		fail("Not yet implemented");
	}

	@Test
	public void testEditBudget() {
		fail("Not yet implemented");
	}

	
	@Test
	public void testGetEverything() {
		fail("Not yet implemented");
	}
	@Test
	public void testDeleteBudget() {
		fail("Not yet implemented");
	}

}
