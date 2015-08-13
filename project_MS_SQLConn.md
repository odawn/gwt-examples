
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

## note about Microsoft JDBC Connector ##
> I have found working with the Microsoft JDBC connector a bit lacking. I wouldn't recommend using MS SQL database, but rather MySQL which is much more flexible and powerful in my opinion. But if you have to connect to a MS SQL database here is how I have done it.


# JDBC MS SQL Connector #
> First you have to uses Microsofts JDBC connector instead of MySQL connector
  * [MS JDBC Connector](http://msdn.microsoft.com/en-us/data/aa937724.aspx) - download and add it to your Java Libraries
  * Be aware that the library and class for 2000 vs 2005 server is different.

# Code Examples #
> MS SQL example methods and classes

> ### Connection Example ###
> > Connecting to MS SQL server with JDBC
```
public class DB_Conn {
	
	private java.sql.Connection con = null;
	private final String url = "jdbc:microsoft:sqlserver://";
	private final String serverName = "ip/host";
	private final String portNumber = "1433";
	private final String databaseName = "catalog";
	private final String userName = "user";
	private final String password = "password";
	
	// Informs the driver to use server a side-cursor, 
	// which permits more than one active statement 
	// on a connection.
	private final String selectMethod = "cursor";

	public DB_Conn() {
	}

    private java.sql.Connection getConnection() {
		try {
                        // Note: this class name changes for ms sql server 2000 thats it
                        // It has to match the JDBC library that goes with ms sql 2000
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(getConnectionUrl());

			if (con != null)
				System.out.println("Connection Successful!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error Trace in getConnection() : "
					+ e.getMessage());
		}
		return con;
	}

	private String getConnectionUrl() {
		String url = "jdbc:sqlserver://" + serverName + ";user=" + userName
				+ ";password=" + password + ";databaseName=" + databaseName
				+ ";";
		return url;
	}

	/*
	     Display the driver properties, database details 
	 */

	public void displayDbProperties() {
		java.sql.DatabaseMetaData dm = null;
		java.sql.ResultSet rs = null;
		try {
			con = this.getConnection();
			if (con != null) {
				dm = con.getMetaData();
				System.out.println("Driver Information");
				System.out.println("\tDriver Name: " + dm.getDriverName());
				System.out
						.println("\tDriver Version: " + dm.getDriverVersion());
				System.out.println("\nDatabase Information ");
				System.out.println("\tDatabase Name: "
						+ dm.getDatabaseProductName());
				System.out.println("\tDatabase Version: "
						+ dm.getDatabaseProductVersion());
				System.out.println("Avalilable Catalogs ");
				rs = dm.getCatalogs();
				while (rs.next()) {
					System.out.println("\tcatalog: " + rs.getString(1));
				}
				rs.close();
				rs = null;
				closeConnection();
			} else
				System.out.println("Error: No active Connection");
		} catch (Exception e) {
			e.printStackTrace();
		}
		dm = null;
	}

	private void closeConnection() {
		try {
			if (con != null)
				con.close();
			con = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		DB_Conn myDbTest = new DB_Conn();
		myDbTest.displayDbProperties();
	}
}
```


> ### Query, Update, Insert ###
> > MS SQL query and update/insert examples using DB\_Conn
```
public class DB_Demo_Queries extends DB_Conn {

	private Connection connection = null;

	// use this to add top 100 to sql field for debugging
	// remove this during update
	private String topQuery = "";

	/**
	 * work on some tables
	 * 
	 * @throws SQLException
	 */
	public DB_Demo_Queries() throws SQLException {

		// open a connection to ms sql db
		connection = this.getConn();

		// during debugging do this
		this.topQuery = " "; // can be 'top 100'

		// condition short data fields
		query1();

		// close the connection when done
		connection.close();
	}

	/**
	 * query example
	 */
	private void query1() {

		String query = "SELECT " + topQuery
				+ " recordID, center FROM dbo.table";

		try {

			Statement select = connection.createStatement();
			ResultSet result = select.executeQuery(query);

			int i = 0; // getResultSetSize(result);
			while (result.next()) {
				String recordID = result.getString(1);
				String center = result.getString(2);

				// make the query and do it
				updateQuery(i, recordID, center);
				i++;
			}
			result.close();

		} catch (Exception e) {

			// debug out output this way
			System.err.println("Query Statement Error: " + query);
			e.printStackTrace();

		}

	}

	/**
	 * update query example
	 * 
	 * @param i
	 * @param recordID
	 * @param center
	 */
	private void updateQuery(int i, String recordID, String center) {

		String sRecordID = recordID;
		String sCenterID = Integer.toString(getCenterID(center));

		String query = "UPDATE table SET CenterID='" + sCenterID
				+ "' WHERE (recordID='" + sRecordID + "');";

		// debug
		System.out.println(i + ". " + query);

		try {

			Statement update = connection.createStatement();
			update.executeUpdate(query);

		} catch (Exception e) {
			// debug out output this way
			System.err.println("Mysql Statement Error: " + query);
			e.printStackTrace();
		}
	}

	/**
	 * query example
	 * 
	 * @param center
	 * @return
	 */
	private int getCenterID(String center) {

		String query = "SELECT CenterID FROM dbo.table2 WHERE (Match='"
				+ center + "');";

		// debug
		System.out.println("Query: " + query);

		int centerID = 0;
		try {

			Statement select = connection.createStatement();
			ResultSet result = select.executeQuery(query);

			while (result.next()) {
				centerID = result.getInt(1);
			}
			result.close();

		} catch (Exception e) {

			// debug out output this way
			System.err.println("Query Statement Error: " + query);
			e.printStackTrace();

		}

		if (centerID == 0) {
			System.out.println("ERROR - NO CENTER for " + center);
		}

		return centerID;
	}

}
```

# Problems With Using MS JDBC #

> Things I don't dig about using MS JDBC

> Pagination, Paging - Getting specific set of records with in a record set should be easy. Its very easy, I mean VERY easy to get them from MySQL. On the other hand its hard to get them from MS SQL. I saw improved code for 2005, but it wasn't much better. I used logic to do a record set off set instead of that weird query below. It makes my code quite pervasive and much more difficult for a anybody else to ready my queries.
```
// MySQL limit clause
SELECT emp_id,lname,fname FROM employee LIMIT 20,10

// MSSQL equivelent
select * from (
 select top 10 emp_id,lname,fname from (
    select top 30 emp_id,lname,fname
    from employee
   order by lname asc
 ) as newtbl order by lname desc
) as newtbl2 order by lname asc
```

> If you want to go to a particular index in the recordset results, you can't do it with MS SQL jdbc. What gives MS. Who wants the entire record set every time. You will have to spool through the entire record set and use a count instead. I do that when I am inserting the record set data into an object array.
```
//This will NOT WORK on ms sql jdbc driver. It will Work with MySQL!
protected static int getResultSetSize(ResultSet resultSet) {
    int size = -1;

    try {
        resultSet.last();
        size = resultSet.getRow();
        resultSet.beforeFirst();
    } catch(SQLException e) {
        return size;
    }

    return size;
}
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
