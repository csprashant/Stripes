package com.nt.model;

public class Address {
	private  String city;
	private  String line1;	
	private  String line2;
	private  String state;
	private  String country;
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		System.out.println();
		this.city = city;
	}
	public String getLine1() {
		return line1;
	}
	public void setLine1(String line1) {
		this.line1 = line1;
	}
	public String getLine2() {
		return line2;
	}
	public void setLine2(String line2) {
		this.line2 = line2;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	


}
