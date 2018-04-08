package org.iiitb.project.web;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;  
import java.util.Date;  

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.iiitb.project.database.DatabaseConnection;

@SuppressWarnings("serial")
public class Account extends HttpServlet{
	
	public int  calculate_Age(String dob) {
		String[] bdate= dob.split("-");
		String birthday=bdate[2]+bdate[1]+bdate[0];
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
	    Date date = new Date();  
	    String t=formatter.format(date);
	    String[] today=t.split("/");
		String now=today[2]+today[1]+today[0];
		return (Integer.parseInt(now)-Integer.parseInt(birthday))/10000;
	}
	
  public void doPost(HttpServletRequest request, HttpServletResponse response)
                                   throws ServletException, IOException {
    response.setContentType("text/html");
    String name = request.getParameter("name");
    String fname = request.getParameter("Father's_name");
    String DOB = request.getParameter("DOB");
    String gender = request.getParameter("gender");
    String Nationality = request.getParameter("nationality");
    String mname = request.getParameter("mothers_name");
    String pan_no = request.getParameter("pan_no");
    String aadhaar = request.getParameter("aadhaar_no");
    String address = request.getParameter("address");
    String city = request.getParameter("city");
    String pin = request.getParameter("pin"); 
    String mobile = request.getParameter("mobile_no");
    
   
    Customer c = new Customer(name,fname,mname,DOB,gender,Nationality,pan_no,aadhaar,address,city,pin,mobile);
    
    DatabaseConnection m=new DatabaseConnection("bankdb","root","sravya");
 	java.sql.PreparedStatement preparedStatement=null;
    try{
    int age=calculate_Age(DOB);
    	
    m.query = "INSERT into Customer VALUES(cust_id,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    preparedStatement = m.conn.prepareStatement(m.query);
    preparedStatement.setString(1,c.name);
    preparedStatement.setString(2,c.fname);
    preparedStatement.setString(3,c.mname);
    preparedStatement.setString(4,c.dob);
    preparedStatement.setInt(5,age);
    preparedStatement.setString(6,c.gender);
    preparedStatement.setString(7, c.pan_no);
    preparedStatement.setString(8,c.aadhaar);
    preparedStatement.setString(9,c.address);
    preparedStatement.setString(10,c.city);
    preparedStatement.setString(11,c.pin);
    preparedStatement.setString(12,c.Nationality);
    preparedStatement.setString(13,c.mobile);
    preparedStatement.setString(14,null);
    preparedStatement.setString(15,null);
    preparedStatement.execute();
	preparedStatement.close();
	m.conn.close();
}
catch (SQLException e) {
	e.printStackTrace();
}
    
  }
}