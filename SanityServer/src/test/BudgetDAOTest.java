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
		Budget Btoadd = new Budget("qwer","yang@usc.edu", DateCal.today(), 10, 1000, 3, 75,toadd );
		try{
			PreparedStatement st = conn.prepareStatement("DELETE FROM SanityDB.Category where Category_name='qqq'");
			st.executeUpdate();
			st = conn.prepareStatement("DELETE FROM SanityDB.Budget where Budget_name='qwer'");
			st.executeUpdate();
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
			}
		
		JSONObject getret =  test.createBudget(Btoadd);
		try{
			PreparedStatement st = conn.prepareStatement("SELECT * FROM SanityDB.Budget WHERE Budget_name=? AND Budget_total=? AND Frequency=?");
			st.setString( 1, "qwer");
			st.setString( 2, "1000");
			st.setString( 3, "3");
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
	public void testEditBudget() {
		ArrayList<Category> toadd = new ArrayList<Category>();
		toadd.add(new Category("qqq", 100 ));
		Budget Btoadd = new Budget("qwer","yang@usc.edu", DateCal.today(), 10, 1000, 3, 75,toadd );
		Budget Btoadd2 = new Budget("testing","yang@usc.edu", DateCal.today(), 10, 800, 5, 75,toadd );
		
		JSONObject getret =  test.editBudget(Btoadd2, Btoadd);
		try{
			PreparedStatement st = conn.prepareStatement("SELECT * FROM SanityDB.Budget WHERE Budget_name=? AND Frequency=?");
			st.setString( 1, "testing");
			st.setString( 2, "5");
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
	public void testGetEverything() throws JSONException {
		User tomake = new User("yang@usc.edu");
		JSONObject toread = test.getEverything(tomake, 1);
		boolean result = true;
		JSONObject Bud = (JSONObject)(toread.getJSONObject("information").getJSONArray("budgetLsit").getJSONObject(0));
		if(!Bud.get("name").toString().equals("testing")){
			result = false;
		}
		if(!Bud.get("date").toString().equals(DateCal.today())){
			result = false;
		}
		if(!Bud.get("period").toString().equals("10")){
			result = false;
		}
		assertEquals(true, result);
	}
	@Test
	public void testDeleteBudget() {
		test.deleteBudget("yang@usc.edu", "testing");
		ArrayList<Category> toadd = new ArrayList<Category>();
		toadd.add(new Category("qqq", 100 ));
		Budget Btoadd = new Budget("testing","yang@usc.edu", DateCal.today(), 10, 1000, 3, 75,toadd );
		try{
			PreparedStatement st = conn.prepareStatement("SELECT* FROM SanityDB.Budget where Budget_Name='testing'");
			ResultSet rs= st.executeQuery();
			JSONObject getret =  test.createBudget(Btoadd);
			assertEquals(false, rs.next());
			
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
			}
		
		System.out.println("136");
		
		
		 try {
			if(conn != null && !conn.isClosed()){
			     conn.close();
			 }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
