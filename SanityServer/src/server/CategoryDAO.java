package server;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

public class CategoryDAO extends DAO{
	private Integer budgetId=-1;
	public void addCategory(Budget budget){
		try{
			insertCategory(budget);
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("Error in insertCategory");
		}
		
	}
	private void insertCategory(Budget budget) throws SQLException{
		Connection conn = getDBConnection();
		PreparedStatement statement = conn.prepareStatement("SELECT * FROM SanityDB.Budget WHERE Budget_name=?");
		statement.setString(1, budget.budgetName);
		try{
			ResultSet rs = statement.executeQuery();
			rs.next();
			budgetId=rs.getInt("Budget_id");
		}catch(SQLException e){
			System.out.println(e.getMessage());
			System.out.println("find budget_id error(add category)");
		}finally{
			if (statement != null) {
				statement.close();
			}
		}
		
		for(int i=0;i<budget.categories.size();++i){
			PreparedStatement st = conn.prepareStatement("INSERT INTO SanityDB.Category(User_id, Category_name, Budget_id,"
					+ "Category_total, Category_spent) VALUE(?,?,?,?,?)");
			st.setInt(1, budget.userId);
			st.setString(2,budget.categories.get(i).categoryName);
			st.setInt(3, budgetId);
			st.setInt(4, budget.categories.get(i).limit);
			st.setInt(5,0);
			try{
				st.executeUpdate();
			}catch(SQLException e){
				System.out.println(e.getMessage());
				System.out.println("insert error(add categories)");
			}finally{
				if (st != null) {
					st.close();
				}
			}
		}
		if(conn!=null){
			conn.close();
		}
	}
}
