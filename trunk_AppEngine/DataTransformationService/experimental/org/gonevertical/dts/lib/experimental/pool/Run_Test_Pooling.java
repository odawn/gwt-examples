package org.gonevertical.dts.lib.experimental.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Run_Test_Pooling {

	// http://svn.apache.org/viewvc/commons/proper/dbcp/trunk/doc/JOCLPoolingDriverExample.java?view=markup
	public static void main(String[] args) {
		
		String sql = "";
		
		String url = "jdbc:apache:commons:dbcp:/db_config";
		 
		Connection conn = null;
		Statement statement = null;
		ResultSet result = null;

		try {
		
			conn = DriverManager.getConnection(url);
			statement = conn.createStatement();
			result = statement.executeQuery(sql);
			
			int numcols = result.getMetaData().getColumnCount();
			
			while(result.next()) {
				
				for(int i=1;i<=numcols;i++) {
					System.out.print("\t" + result.getString(i));
				}
				
				System.out.println("");
				
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
			
		} finally {
			
			try { if (result != null) result.close(); } catch(Exception e) { }
			try { if (statement != null) statement.close(); } catch(Exception e) { }
			try { if (conn != null) conn.close(); } catch(Exception e) { }
			
		}
		
	}
	
}
