package org.iiitb.project.database;
import java.sql.*;


public class DatabaseConnection{
	public Statement stmt;
	public ResultSet rs;
	public Connection conn=null;
	public String query=null;
	public DatabaseConnection(String database,String user,String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			//System.out.println("Driver Found");
		}

		catch (ClassNotFoundException e) {
			System.out.println("Driver Not Found: " + e);
		}
        String URL="jdbc:mysql://localhost:3306/"+database+"?verifyServerCertificate=false&useSSL=true";
        conn=null;
		try {
			conn = (Connection)DriverManager.getConnection(URL,user,password);
			//System.out.println("Successfully Connected to Database");
		} catch (SQLException e) {
			System.out.println("SQL Exception: " + e);
		}

	}
}