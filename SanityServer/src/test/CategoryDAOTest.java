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
public class CategoryDAOTest {
	CategoryDAO test = new CategoryDAO();
	Connection conn;
	@Before
	public void setUp() throws Exception {
		conn = test.getDBConnection();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddSingleCategory() {
		try{
			PreparedStatement st = conn.prepareStatement("DELETE FROM SanityDB.Category where Category_name='testing'");
			st.executeUpdate();
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
			}
		test.addSingleCategory("yang@usc.edu", "testing", "testing", 200.0);
		try{
			PreparedStatement st = conn.prepareStatement("SELECT* FROM SanityDB.Category where Category_name='testing' AND Category_total = '200'");
			ResultSet rs = st.executeQuery();
			assertEquals(true, rs.next());
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
			}
		
	}

	@Test
	//Bug with check category, wrong entry.
	//wrong check condition in line 129, categoryDAO
	public void testEditCategory() {
		test.editCategory("yang@usc.edu", "testing", "testing", "testing", 150.0);
		try{
			PreparedStatement st = conn.prepareStatement("SELECT* FROM SanityDB.Category where Category_name='testing' AND Category_total = '150'");
			ResultSet rs = st.executeQuery();
			assertEquals(true, rs.next());
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
			}
		
	}
	
	
	@Test
	public void testDeleteCategories() {
		test.deleteCategories("yang@usc.edu", "testing", "testing");
		try{
			PreparedStatement st = conn.prepareStatement("SELECT* FROM SanityDB.Category where Category_name='testing' AND Category_total = '150'");
			ResultSet rs = st.executeQuery();
			assertEquals(true, rs.next());
			
		}catch(SQLException e){
			System.out.println(e.getMessage());
			}
	}


}
