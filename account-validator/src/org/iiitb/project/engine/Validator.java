package org.iiitb.project.engine;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

import org.iiitb.project.database.DatabaseConnection;
import org.iiitb.project.methods.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.json.*;
import javax.xml.parsers.*;
import java.lang.reflect.*;

public class Validator{
	
	DatabaseConnection db;
	String user;
	String password;
	String dbName;
	String file,table;
	Document doc;
	String pk;
	
	int validate(String cust_value,String op,String given_value)
	{
		if(op.equals(">") | op.equals("greater than"))                                               //If operator is ">" or "greater than"
			if(Integer.parseInt(cust_value) > Integer.parseInt(given_value))
				return 1;
			else
				return 0;
		else if(op.equals("<") | op.equals("less than"))											//If operator is "<"  or "less than"
			if(Integer.parseInt(cust_value) < Integer.parseInt(given_value))
				return 1;
			else
				return 0;
		else if(op.equals("!=") | op.equals("not equals") | op.equals("not matches"))
			if(cust_value.equals(given_value))														//If operator is "!=" or "not equals" or "not matches"
				return 0;
			else
				return 1;
		else if(op.equals("=") | op.equals("match") | op.equals("equals"))							//If operator is "=" or "Equals" or "matches" 
			if(cust_value.equals(given_value))
				return 1;
			else
				return 0;
		else if(op.equals("minimum") | op.equals(">=")) 											//If operator is ">=" minimum
			if(Integer.parseInt(cust_value) >= Integer.parseInt(given_value))
				return 1;
			else
				return 0;
		else if(op.equals("maximum") | op.equals("<="))												//If operator is "<=" maximum
			if(Integer.parseInt(cust_value) <= Integer.parseInt(given_value) )
				return 1;
			else
				return 0;
		else 
			return 0;
		
	}
	String getValue(Element vElement,String table_name,int id)
	{
		String req_value = " ";
		java.sql.PreparedStatement preparedStatement=null;
	 	if(vElement.getElementsByTagName("ref").getLength()!=0)   				//If rule has "ref" element
       	{
       		Node ref = vElement.getElementsByTagName("ref").item(0);
       		Element Ref = (Element) ref;
       		String ref_table = Ref.getElementsByTagName("table").item(0).getTextContent();
       		String ref_column = Ref.getElementsByTagName("column").item(0).getTextContent();
       		String join_key = Ref.getElementsByTagName("join_key").item(0).getTextContent();
       		try {
       			String key_value="";
       			db.query = "select " +join_key + " from " + table_name + " where " + pk + " =?";
       			preparedStatement = db.conn.prepareStatement(db.query);
       			preparedStatement.setInt(1,id);
       			db.rs = preparedStatement.executeQuery();
       			if(db.rs.next()) {
       					key_value = db.rs.getString(join_key);
       			}
     
    			db.query = "select "+ ref_column +" from " + ref_table + " where " + join_key + " =?";
    			preparedStatement=db.conn.prepareStatement(db.query);
    			preparedStatement.setString(1,key_value);
    			db.rs =  preparedStatement.executeQuery();
    				while(db.rs.next())
    				{
    					req_value=db.rs.getString(ref_column);
    				}
    			}
    		catch(Exception e) {
    				e.printStackTrace();
    			}    
       	}
       	
       	else {
       		req_value = vElement.getElementsByTagName("ind").item(0).getTextContent();
       	}
	 	return req_value;
	}
	
	int getResult(int id,Node condtnNode)
	{
		String table_name="";
		String column_name="";
		String operator="";
		String req_value="";
		String cust_value=" ";
		java.sql.PreparedStatement preparedStatement=null;
		int flag=0;
		
		Element cElement = (Element) condtnNode;
		
		table_name=cElement.getElementsByTagName("table").item(0).getTextContent();
		column_name=cElement.getElementsByTagName("column").item(0).getTextContent();
		operator=cElement.getElementsByTagName("operator").item(0).getTextContent();
		Node value = cElement.getElementsByTagName("value").item(0);
		Element vElement = (Element) value;
		req_value = getValue(vElement,table_name,id);
		
		try {
			db.query = "select "+ column_name +" from " + table_name + " where " + pk + " = ?";
			preparedStatement=db.conn.prepareStatement(db.query);
			preparedStatement.setInt(1, id);
			db.rs =  preparedStatement.executeQuery();
				while(db.rs.next())
				{
					cust_value=db.rs.getString(column_name);
				}
			flag = validate(cust_value, operator, req_value);
			}
		catch(Exception e) {
				e.printStackTrace();
			}
		return flag;
	}
	
	void validateForm_row(int id,String table) {
		
		try {
			//Loading the rule file
			File input = new File(file);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document doc = dbBuilder.parse(input);
			doc.getDocumentElement().normalize();//Normalize the root element
			System.out.print("Root : " + doc.getDocumentElement().getNodeName());
			
			//Reading the rules to a list
			NodeList nList = doc.getElementsByTagName("RULE");
			
	
			//@SuppressWarnings("unused")
			ArrayList<String> action_list=new ArrayList<String>();
			JSONObject json = new JSONObject();
			
			java.sql.PreparedStatement preparedStatement=null;
			int flag=0;
        	 
			
			for(int i =0 ; i< nList.getLength(); i++) {
					
					Node ruleNode = nList.item(i);
					
					System.out.println("\nCurrent rule :" + ruleNode.getAttributes().item(0).getNodeValue());
			        action_list.clear(); 
			         
			        if (ruleNode.getNodeType() == Node.ELEMENT_NODE) {
			             
			        	   	Element eElement = (Element) ruleNode;
			               
			               	if(eElement.getElementsByTagName("AND").getLength()!=0)   				//If rule has "AND" element
			               	{
			               		Node and = eElement.getElementsByTagName("AND").item(0);
			               		Element And = (Element) and;
			               		NodeList andList = And.getElementsByTagName("CONDITION");
			               		
			               		if(andList.getLength()!=0)
			               			flag=1;
			               		
			               		for(int j=0;j<andList.getLength();j++) {
			               		
			               			Node cond = andList.item(j);
			               		
			               			if(cond.getNodeType() == Node.ELEMENT_NODE) 
			               			{
						            	if(getResult(id,cond)==0) 
						            	{
						            		flag=0;
						            		break;
						            	}
			               			}
			               	
			               		}
			               	}	
			               	
			               	else if(eElement.getElementsByTagName("OR").getLength()!=0)				//If rule has "OR" element
			               	{
			               		Node or = eElement.getElementsByTagName("OR").item(0);
			               		Element Or = (Element) or;
			               		NodeList orList = Or.getElementsByTagName("CONDITION");
			               		
			               		if(orList.getLength()!=0)flag=0;
			               		
			               		for(int j=0;j<orList.getLength();j++) {
			               			Node cond = orList.item(j);
			               			if(cond.getNodeType() == Node.ELEMENT_NODE)
			               			{
						            	if(getResult(id,cond)==1) 
						            	{
						            		flag=1;
						            		break;
						            	}
			               		   }
			               		}
			               	}
			               	else
			               	{
			               		NodeList condtnNodeList = eElement.getElementsByTagName("CONDITION");
			               		for(int j=0;j<condtnNodeList.getLength();j++)
			               		{
			               			Node condtnNode = condtnNodeList.item(j);
			               			flag = getResult(id,condtnNode);
					            				               		
			               		}
			               	}
			               	
			           //Reading actions from ActionList    	
			           NodeList actionList = eElement.getElementsByTagName("ActionList");
			               			for(int k=0; k < actionList.getLength(); k++)
			               			{
			               				Node actionNode = actionList.item(k);
			               				Element aElement = (Element) actionNode;
			               				
			               				action_list.add(aElement.getElementsByTagName("action").item(0).getTextContent());
			               			}
			            }  
			            //performing actions 
			            try {
			            	
			            	if(flag==1)
			            	{
			            		db.query = "select * from " + table + " where " + pk + "= ?";
			            		preparedStatement = db.conn.prepareStatement(db.query);
			            		preparedStatement.setInt(1, id);
			            		ResultSet rs = preparedStatement.executeQuery();
			            		ResultSetMetaData rsdm = rs.getMetaData();
			            		
			            		while(rs.next()) {
			            			
			            			for(int t=1;t<=rsdm.getColumnCount();t++) {
			            				
			            				String col = rsdm.getColumnName(t);
			            				String val = rs.getString(col);
			            				json.put(col,val);	
			            			}
			            			
			            		}
			            		
			            		for(int a=0;a<action_list.size();a++)
			            		{
			            			callFun(action_list.get(a),json);
			            		}
			            		
			            		while(json.length()>0)
			            			   json.remove((String) json.keys().next());
			            	}	
			            }
						 catch(SQLException e){
							 e.printStackTrace();
				 }  
			  }//end of each rule
			
			}
		
		catch(Exception e){
			e.printStackTrace();
		 }
	}

	void validateForm_rule(String table) {
			
			try {
				//Loading the rule file
				File input = new File(file);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
				Document doc = dbBuilder.parse(input);
				doc.getDocumentElement().normalize();//Normalize the root element
				System.out.print("Root : " + doc.getDocumentElement().getNodeName());
				
				//Reading the rules to a list
				NodeList nList = doc.getElementsByTagName("RULE");
				
		
				//@SuppressWarnings("unused")
				ArrayList<String> action_list=new ArrayList<String>();
				JSONObject json;
				java.sql.PreparedStatement preparedStatement=null;	        	 
				
				for(int i =0 ; i< nList.getLength(); i++) {
						
						Node ruleNode = nList.item(i);
						
						System.out.println("\nCurrent rule :" + ruleNode.getAttributes().item(0).getNodeValue());
				        action_list.clear(); 
				         
				        if (ruleNode.getNodeType() == Node.ELEMENT_NODE) {
				             
				        	   	Element eElement = (Element) ruleNode;
				        	   	
				        	    NodeList actionList = eElement.getElementsByTagName("ActionList");
		               			for(int k=0; k < actionList.getLength(); k++)
		               			{
		               				Node actionNode = actionList.item(k);
		               				Element aElement = (Element) actionNode;
		               				
		               				action_list.add(aElement.getElementsByTagName("action").item(0).getTextContent());
		               				//System.out.println(action_list.get(0));
		               			}
				               
				               	if(eElement.getElementsByTagName("AND").getLength()!=0)   				//If rule has "AND" element
				               	{
				               		Node and = eElement.getElementsByTagName("AND").item(0);
				               		Element And = (Element) and;
				               		NodeList andList = And.getElementsByTagName("CONDITION");
				               		
				               		if(andList.getLength()!=0)
					               		db.query = "";
				               			
				               		for(int j=0;j<andList.getLength();j++) {
				               		
				               			Node cond = andList.item(j);
				               		
				               			if(cond.getNodeType() == Node.ELEMENT_NODE) 
				               			{
				               				String table_name="";
				               				String column_name="";
				               				String operator="";
				               				String req_value="";
				               				
				               				Element cElement = (Element) cond;
				               				
				               				table_name=cElement.getElementsByTagName("table").item(0).getTextContent();
				               				column_name=cElement.getElementsByTagName("column").item(0).getTextContent();
				               				operator=cElement.getElementsByTagName("operator").item(0).getTextContent();
				               				Node value = cElement.getElementsByTagName("value").item(0);
				               				Element vElement = (Element) value;
				               				
				               			 	if(vElement.getElementsByTagName("ref").getLength()!=0)   				//If rule has "ref" element
				               		       	{
				               		       		Node ref = vElement.getElementsByTagName("ref").item(0);
				               		       		Element Ref = (Element) ref;
				               		       		String ref_table = Ref.getElementsByTagName("table").item(0).getTextContent();
				               		       		String ref_column = Ref.getElementsByTagName("column").item(0).getTextContent();
				               		       		String join_key = Ref.getElementsByTagName("join_key").item(0).getTextContent();
				               		       		if(j==0)
				               		       				db.query =  " select * from " + table_name + " where " + column_name + " " +operator +" (select " + ref_column + " from " + ref_table +" where " + table_name+"."+join_key +" = " + ref_table +"."+join_key + ")" ;
				               		       		else {
				               		       				db.query = db.query + " AND " + column_name + " " +operator +" (select " + ref_column + " from " + ref_table +" where " + table_name+"."+join_key +" = " + ref_table +"."+join_key+")";
				               		       			}
				               				
				               		       	}
				               		       	
				               		       	else {
				               		       		req_value = vElement.getElementsByTagName("ind").item(0).getTextContent();
				               		       			if(j==0)
				               		       			db.query = " select * from " + table_name + " where " + column_name + " " + operator + " " + "'" + req_value +"'";
				               		       			else
				               		       				db.query = db.query + " AND " + column_name + " " + operator + " " + "'"+req_value +"'";	
			               		           		
				               		       	}
				         
				               			}
				               	
				               		}
				  
               		       			
				               	}	
				               	
				               	else if(eElement.getElementsByTagName("OR").getLength()!=0)				//If rule has "OR" element
				               	{
				               		Node or = eElement.getElementsByTagName("OR").item(0);
				               		Element Or = (Element) or;
				               		NodeList orList = Or.getElementsByTagName("CONDITION");
				               		
				               		if(orList.getLength()!=0)db.query ="";
				               			
				               		for(int j=0;j<orList.getLength();j++) {
				               			Node cond = orList.item(j);
				               			if(cond.getNodeType() == Node.ELEMENT_NODE)
				               			{

				               				String table_name="";
				               				String column_name="";
				               				String operator="";
				               				String req_value="";
				               				
				               				Element cElement = (Element) cond;
				               				
				               				table_name=cElement.getElementsByTagName("table").item(0).getTextContent();
				               				column_name=cElement.getElementsByTagName("column").item(0).getTextContent();
				               				operator=cElement.getElementsByTagName("operator").item(0).getTextContent();
				               				Node value = cElement.getElementsByTagName("value").item(0);
				               				Element vElement = (Element) value;
				               				
				               			 	if(vElement.getElementsByTagName("ref").getLength()!=0)   				//If rule has "ref" element
				               		       	{
				               		       		Node ref = vElement.getElementsByTagName("ref").item(0);
				               		       		Element Ref = (Element) ref;
				               		       		String ref_table = Ref.getElementsByTagName("table").item(0).getTextContent();
				               		       		String ref_column = Ref.getElementsByTagName("column").item(0).getTextContent();
				               		       		String join_key = Ref.getElementsByTagName("join_key").item(0).getTextContent();
				               		       		if(j==0)
			               		       				db.query =  " select * from " + table_name + " where " + column_name + " " +operator +" (select " + ref_column + " from " + ref_table +" where " + table_name+"."+join_key +" = " + ref_table +"."+join_key + ")" ;
			               		       			else {
			               		       				db.query = db.query + " OR " + column_name + " " +operator +" (select " + ref_column + " from " + ref_table +" where " + table_name+"."+join_key +" = " + ref_table +"."+join_key+")";
			               		       			}
				               		       		    
				               		       	}
				               		       	
				               		       	else {
				               		       		req_value = vElement.getElementsByTagName("ind").item(0).getTextContent();
				               		       		if(j==0)
				               		       			db.query = " select * from " + table_name + " where " + column_name + " " + operator + " " + "'" + req_value +"'";
				               		       			else
				               		       				db.query = db.query + " OR " + column_name + " " + operator + " " + "'"+req_value +"'";
				               		       		    

				               		       	}
				               		   }
				               		}
				               	}
				               	else
				               	{
				               		NodeList condtnNodeList = eElement.getElementsByTagName("CONDITION");
				               		for(int j=0;j<condtnNodeList.getLength();j++)
				               		{
				               			Node condtnNode = condtnNodeList.item(j);

			               				String table_name="";
			               				String column_name="";
			               				String operator="";
			               				String req_value="";
			               				String cust_value=" ";
			               				
			               				Element cElement = (Element) condtnNode;
			               				
			               				table_name=cElement.getElementsByTagName("table").item(0).getTextContent();
			               				column_name=cElement.getElementsByTagName("column").item(0).getTextContent();
			               				operator=cElement.getElementsByTagName("operator").item(0).getTextContent();
			               				Node value = cElement.getElementsByTagName("value").item(0);
			               				Element vElement = (Element) value;
			               				
			               			 	if(vElement.getElementsByTagName("ref").getLength()!=0)   				//If rule has "ref" element
			               		       	{
			               		       		Node ref = vElement.getElementsByTagName("ref").item(0);
			               		       		Element Ref = (Element) ref;
			               		       		String ref_table = Ref.getElementsByTagName("table").item(0).getTextContent();
			               		       		String ref_column = Ref.getElementsByTagName("column").item(0).getTextContent();
			               		       		String join_key = Ref.getElementsByTagName("join_key").item(0).getTextContent();
			               		       		db.query = "select * from " + table_name + " where " + column_name + operator +" (select " + ref_column + " from " + ref_table +" where " + table_name+"."+join_key +" = " + ref_table +"."+join_key + ")"  ;
			               		       		    
			               		       	}
			               		       	
			               		       	else {
			               		       		req_value = vElement.getElementsByTagName("ind").item(0).getTextContent();
			               		       		db.query = "select * from " + table_name + " where " + column_name + " " + operator + " " + "'"+req_value + "'";

			               		       	}
						            				               		
				               		}
				               		
				               	}
				               	
				           //Reading actions from ActionList    	
				          
				            }  
				        preparedStatement = db.conn.prepareStatement(db.query);
   		       			db.rs = preparedStatement.executeQuery();
   		       			ResultSetMetaData rsdm = db.rs.getMetaData();
   		       			JSONArray array = new JSONArray();
   		       			int flag = 0;
   		       			while(db.rs.next()) {
   		       					flag = 1;
   		       					json = new JSONObject();
   		       					for(int t = 1;t<rsdm.getColumnCount();t++) {
   		       						String col = rsdm.getColumnName(t);
   		       						String val = db.rs.getString(col);
   		       						json.put(col, val);
   		       					}
   		       				   array.put(json);
   		       			}
   		       			
   		       			JSONObject res = new JSONObject();
   		       			
   		       			res.put("result", array);
   		       			
   		       			//System.out.println(res);
            			
   		       			if(flag==1) {
   		       				for(int k=0;k<action_list.size();k++) {
   		       					callFun(action_list.get(k),res);
   		       				}
   		       				//callFun(ac);
   		       			}
   		       			
					 }  
				  //end of each rule
				
				}
			
			catch(Exception e){
				e.printStackTrace();
			 }
		
	}
void callFun(String fname,JSONObject obj)
{
	Actions fns=new Actions();
	@SuppressWarnings("rawtypes")
	Class cls = fns.getClass();
	
	try {
		
		@SuppressWarnings("unchecked")
		Method method_call = cls.getDeclaredMethod(fname,JSONObject.class);
		method_call.invoke(fns, obj);
	}
	
	catch(Exception e) {
		
		e.printStackTrace();
	
	}
}
	
void rejectForm(int id) {
		
		java.sql.PreparedStatement preparedStatement = null;
		
		try {
			db.query = "update " + table + " set status = ? where " + pk + " = ?";
			preparedStatement = db.conn.prepareStatement(db.query);
			preparedStatement.setString(1,"reject");
			preparedStatement.setInt(2,id);
			preparedStatement.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		
	}
	
void acceptForm(int id) {
		
		java.sql.PreparedStatement preparedStatement = null;
		
		try {
			db.query = "update " + table +" set status = ? where " + pk + " = ?";
			preparedStatement = db.conn.prepareStatement(db.query);
			preparedStatement.setString(1,"accept");
			preparedStatement.setInt(2,id);
			preparedStatement.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Validator(String user,String password,String datbase,String table,String file,int method) {
		
			this.user = user;
			this.password = password;
			dbName = datbase;
			this.file = file;
			db=new DatabaseConnection(dbName,user,password);
			java.sql.PreparedStatement preparedStatement = null;
			this.table = table;
			pk = null;
			
			try {
				File input = new File(file);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
				doc = dbBuilder.parse(input);
			}
			
			catch(Exception e) {
				e.printStackTrace();
			}
			
			if(method == 1) {
				try {
					db.query = "select COLUMN_NAME from information_schema.COLUMNS where (TABLE_SCHEMA = ?) AND (TABLE_NAME=?) AND (COLUMN_KEY = 'PRI')";
					preparedStatement = db.conn.prepareStatement(db.query);
					preparedStatement.setString(1, dbName);
					preparedStatement.setString(2, table);
					ResultSet rS = preparedStatement.executeQuery();
					
					if(rS.next()) {
						
						pk = rS.getString("COLUMN_NAME");
				
					db.query = "select " + pk +" from " + table;
					preparedStatement = db.conn.prepareStatement(db.query);
					ResultSet RS = preparedStatement.executeQuery();
					while(RS.next()) {
						validateForm_row(RS.getInt(pk),table);					
					}	
					}	
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			}
			
			else {
				//System.out.println("Innnnn rulewise ");
				validateForm_rule(table);
				
			}
		}
}
