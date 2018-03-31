package org.iiitb.project.web;
public class Customer {
	String name;
	String fname;
	String mname;
	String dob;
	String gender;
	String Nationality;
	String pan_no;
	String aadhaar;
	String address;
	String city;
	String pin;
	String mobile;
	
	Customer(String c_name,String c_fname,String c_mname,String c_dob,String c_gender,String c_nationality,String c_pan,String c_aadhaar,String c_address,String c_city,String c_pin,String mobile){
		
			this.name = c_name;
			this.fname = c_fname;
			this.aadhaar = c_aadhaar;
			this.address = c_address;
			this.mobile = mobile;
			this.dob=c_dob;
			this.gender=c_gender;
			this.city=c_city;
			this.Nationality=c_nationality;
			this.pan_no=c_pan;
			this.pin=c_pin;
			this.mname=c_mname;
		
	}
}
