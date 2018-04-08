package org.iiitb.project.engine;

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class InferenceEngine extends JFrame implements ActionListener{
	
	JLabel dbUser,dbPassword,dbName,rulesFile,tableName;
	JTextField user_field,dbName_field,rules_field,tableName_field;
	JPasswordField password_field;
	JButton run,browseButton,configButton;
	JRadioButton row_wise,rule_wise;
	ButtonGroup group;
	JFileChooser browse,config;
	int rFlag;
	public InferenceEngine() {
		
		rFlag = 1;
		
		dbUser = new JLabel("Database Username");
		dbUser.setBounds(10,10,150,20);
		add(dbUser);
		
		user_field =new JTextField();
		user_field.setBounds(180,10,120,20);
		add(user_field);
		
		dbPassword = new JLabel("Database Password");
		dbPassword.setBounds(10,35,150,20);
		add(dbPassword);
		
		password_field =new JPasswordField();
		password_field.setBounds(180,35,120,20);
		add(password_field);
		
		dbName = new JLabel("Database Name");
		dbName.setBounds(10,70,150,20);
		add(dbName);
		
		dbName_field =new JTextField();
		dbName_field.setBounds(180,70,120,20);
		add(dbName_field);
		
		tableName = new JLabel("Table Name");
		tableName.setBounds(10,105,150,20);
		add(tableName);
		
		tableName_field =new JTextField();
		tableName_field.setBounds(180,105,120,20);
		add(tableName_field);
		
		
		
		rulesFile = new JLabel("Rules File");
		rulesFile.setBounds(10,145,150,20);
		add(rulesFile);
		
		rules_field =new JTextField();
		rules_field.setBounds(180,145,120,20);
		add(rules_field);
		
		browse = new JFileChooser("Browse");
		
		browseButton = new JButton("Browse");
		browseButton.setBounds(310,145,60,20);
	
		browseButton.addActionListener(this);
		add(browseButton);
		
		config = new JFileChooser("Select Config file");
		
		configButton = new JButton("Config file");
		configButton.setBounds(10,180,90,20);
	
		configButton.addActionListener(this);
		add(configButton);
		
		run = new JButton("Run Validator");
		run.setBounds(160,180,200,40);
		run.addActionListener(this);
		add(run);
		
		row_wise = new JRadioButton("row-wise",true);
		row_wise.setBounds(10,220,120,30);
		row_wise.addActionListener(this);
		rule_wise = new JRadioButton("rule-wise",false);
		rule_wise.setBounds(10,260,120,30);
		rule_wise.addActionListener(this);
		group = new ButtonGroup();
		group.add(row_wise);
		group.add(rule_wise);
		
		add(row_wise);
		add(rule_wise);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400,500);
		setLayout(null);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == run ) {
			try {
				String user = user_field.getText();
				String file = rules_field.getText();
				String database = dbName_field.getText();
				@SuppressWarnings("deprecation")
				String password = password_field.getText();
				String table = tableName_field.getText();
				
				
				
				new Validator(user,password,database,table,file,rFlag);
				
				}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
		else if(e.getSource() == browseButton) {
			
			int retValue = browse.showOpenDialog(InferenceEngine.this);
			if(retValue == JFileChooser.APPROVE_OPTION) {
				File file = browse.getSelectedFile();
				rules_field.setText(file.getPath());
			}
		}
		
		else if(e.getSource() == configButton) {
			int value = config.showOpenDialog(InferenceEngine.this);
			if(value == JFileChooser.APPROVE_OPTION) 
			{
				File f = config.getSelectedFile();
				try {
				BufferedReader in = new BufferedReader(new FileReader(f.getPath()));
				String line;
				String[] data;
				while((line = in.readLine()) != null)
				{
				    data=line.split(":");
				    if(data[0].equals("user")) {
				    	user_field.setText(data[1]);
				    }
				    else if(data[0].equals("password")) {
				    	password_field.setText(data[1]);
				    }
				    else if(data[0].equals("database")) {
				    	dbName_field.setText(data[1]);
				    }
				    else if(data[0].equals("table")) {
				    	tableName_field.setText(data[1]);
				    }
				    else if(data[0].equals("rulesFile")) {
				    	rules_field.setText(data[1]);
				    }
				}
				in.close();
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			
		}
		else if(e.getSource() == row_wise)
			rFlag = 1;
		else if(e.getSource() == rule_wise)
			rFlag = 0;
		
		
	}
	
	public static void main(String args []) {
		
		new InferenceEngine();
		
	}

}
