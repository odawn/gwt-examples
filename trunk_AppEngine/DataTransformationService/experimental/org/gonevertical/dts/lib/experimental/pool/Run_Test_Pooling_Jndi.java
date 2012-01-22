package org.gonevertical.dts.lib.experimental.pool;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;

public class Run_Test_Pooling_Jndi {


	public static void main(String[] args) {	
		test();
	}

	
	public static void test() {
		
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
		
		System.setProperty(Context.PROVIDER_URL, "file:///Users/branflake2267/tmp");
		
		InitialContext ic = null;
    try {
	    ic = new InitialContext();
    } catch (NamingException e) {
	    e.printStackTrace();
    }

		// Construct BasicDataSource reference
		Reference ref = new Reference("javax.sql.DataSource", "org.apache.commons.dbcp.BasicDataSourceFactory", null);
		ref.add(new StringRefAddr("driverClassName", "com.mysql.jdbc.Driver"));
		ref.add(new StringRefAddr("type", "javax.sql.DataSource"));
		ref.add(new StringRefAddr("url", "jdbc:mysql://ark/system?autoReconnect=true"));
		ref.add(new StringRefAddr("username", "Web"));
		ref.add(new StringRefAddr("password", "?"));
		
		ref.add(new StringRefAddr("removeAbandoned", "true"));
		ref.add(new StringRefAddr("removeAbandonedTimeout", "90"));
		ref.add(new StringRefAddr("logAbandoned", "true"));
		ref.add(new StringRefAddr("maxActive", "1000"));
		ref.add(new StringRefAddr("maxIdle", "30"));
		ref.add(new StringRefAddr("maxWait", "900"));
		
		try {
	    ic.rebind("jdbc/basic", ref);
    } catch (NamingException e) {

	    e.printStackTrace();
    }

		
		InitialContext ic2 = null;
    try {
	    ic2 = new InitialContext();
    } catch (NamingException e) {
	    e.printStackTrace();
    }
    
		DataSource ds = null;
    try {
	    ds = (DataSource) ic2.lookup("jdbc/basic");
    } catch (NamingException e) {
	    e.printStackTrace();
    }
		
		Connection conn = null;
    try {
	    conn = ds.getConnection();
    } catch (SQLException e) {
    	e.printStackTrace();
    }
		
		try {
	    conn.close();
    } catch (SQLException e) {
	    e.printStackTrace();
    }
		
	}
	
}
