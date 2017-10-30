package test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.BudgetDAO;
import server.DAO;
import server.User;
import server.UserDAO;

public class UserDAOTest {
	UserDAO tests = new UserDAO();
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
	public void testRegister() throws JSONException {
		BudgetDAO Bud = new BudgetDAO();
		Bud.deleteBudget("yang@usc.edu", "qwer");
		Bud.deleteBudget("yang@usc.edu", "testing");
		User toadd = new User("yang", "yang@usc.edu", "123", "456");
		
		
		try{
			PreparedStatement st = conn.prepareStatement("DELETE FROM SanityDB.User where Email='yang@usc.edu'");
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
	public void testLogin() throws JSONException {
		User toadd = new User("yang", "yang@usc.edu", "123", "456");
		
		JSONObject getret =  tests.Login(toadd);
		System.out.println("\n Result for login: "+ getret.get("status") + "\n");
		assertEquals("success", getret.get("status"));
	}

	@Test
	public void testCheckUserExist() throws SQLException {
		User toadd = new User("yang", "yang@usc.edu", "123", "456");
	
		boolean t = true;
		try{
			PreparedStatement st = conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=? ");
			st.setString( 1, "yang@usc.edu");
			ResultSet rs = st.executeQuery();
			if(!rs.next()){
				fail("Email doesn't exist");
			}
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
		boolean a = tests.checkUserExist(toadd);
		System.out.println("User exist for yang@usc.edu: " + tests.checkUserExist(toadd)+"\n");
		assertEquals(t, a);
	}


	@Test
	public void testChangeUsername() throws SQLException {
		User toadd = new User("chen", "yang@usc.edu", "123", "456");
		
		try{
			PreparedStatement st = conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=? ");
			st.setString( 1, "yang@usc.edu");
			ResultSet rs = st.executeQuery();
			if(!rs.next()){
				fail("Email doesn't exist");
			}
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
		tests.changeUsername(toadd);
		try{
			PreparedStatement st = conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=? AND Username = ?");
			st.setString( 1, "yang@usc.edu");
			st.setString( 2, "chen");
			ResultSet rs = st.executeQuery();
			assertEquals(true, rs.next());
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
		
		
	}

	@Test
	public void testChangePassword() throws SQLException {
		User toadd = new User("chen", "yang@usc.edu", "123", "456");
		User toadd2 = new User("chen", "yang@usc.edu", "321", "654");
		
		try{
			PreparedStatement st = conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=? ");
			st.setString( 1, "yang@usc.edu");
			ResultSet rs = st.executeQuery();
			if(!rs.next()){
				fail("Email doesn't exist");
			}
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
		tests.changePassword(toadd, toadd2);
		try{
			PreparedStatement st = conn.prepareStatement("SELECT * FROM SanityDB.User WHERE Email=? AND Password1= ? and Password2 = ?");
			st.setString( 1, "yang@usc.edu");
			st.setString( 2, "321");
			st.setString( 3, "654");
			ResultSet rs = st.executeQuery();
			boolean result= false;;
			if(rs.next()){
				result=true;
			}
			
			assertEquals(true,result);
			
			
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
		}
		
	}

}
