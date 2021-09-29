package com.nt.model;

public class CustomerData {
	private String name;
	private String email;
	private String customerId;
	private String phone;
	public CustomerData() {
	
	}
	
	
	public CustomerData(String name, String email, String customerId, String phone) {
		super();
		this.name = name;
		this.email = email;
		this.customerId = customerId;
		this.phone = phone;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
