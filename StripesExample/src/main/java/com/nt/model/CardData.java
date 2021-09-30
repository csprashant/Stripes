package com.nt.model;

public class CardData {
	private String id;
	private String number;
	private String month;
	private String year;
	private String cvc;
	public CardData() {
		// TODO Auto-generated constructor stub
	}
	public CardData(String id, String number, String month, String year, String cvc) {
		super();
		this.id = id;
		this.number = number;
		this.month = month;
		this.year = year;
		this.cvc = cvc;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getCvc() {
		return cvc;
	}
	public void setCvc(String cvc) {
		this.cvc = cvc;
	}
	
	

}
