package com.nt.model;

public class ChargeData {
	private String id;
	private Long amount;
	private Boolean captured;
	private String currency;
	public ChargeData() {
		// TODO Auto-generated constructor stub
	}
	
	public ChargeData(String id, Long amount, Boolean captured, String currency) {
		super();
		this.id = id;
		this.amount = amount;
		this.captured = captured;
		this.currency = currency;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public Boolean getCaptured() {
		return captured;
	}
	public void setCaptured(Boolean captured) {
		this.captured = captured;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	

}
