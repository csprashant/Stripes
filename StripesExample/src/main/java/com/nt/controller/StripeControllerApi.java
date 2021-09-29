package com.nt.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nt.model.CustomerData;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
@RestController
@RequestMapping("/api")
public class StripeControllerApi {
	@Value("${stripe.apikey}")
	String stripeKey;
	@PostMapping("/customer")
	public CustomerData createCustomer(@RequestBody CustomerData data) throws StripeException {
		Stripe.apiKey=stripeKey; 
		Map<String ,Object> customerParam=new HashMap<String,Object>();  
		  customerParam.put("email", data.getEmail());
		  customerParam.put("name", data.getName());
		  customerParam.put("phone",data.getPhone());
		  Customer customer=Customer.create(customerParam);
		  data.setCustomerId(customer.getId());
		  return data;
	}
	@GetMapping("/customer/{custid}")
	public CustomerData getCustomer(@PathVariable String custid)  throws StripeException {
		Stripe.apiKey=stripeKey; 
		System.out.println(custid);
		Customer existingCustomer=Customer.retrieve(custid);
		CustomerData data=new CustomerData();
		if(existingCustomer!=null) {
			data.setCustomerId(existingCustomer.getId());
			data.setName(existingCustomer.getName());
			data.setEmail(existingCustomer.getEmail());
			data.setPhone(existingCustomer.getPhone());
			return data;
		}
		else
			throw new RuntimeException("No customer fond with id "+custid);
			
	}
	@PutMapping("/customer/{custid}")
	public CustomerData updateCustomer(@PathVariable String custid,@RequestBody CustomerData data )  throws StripeException {
		Stripe.apiKey=stripeKey; 
		System.out.println(custid);
		Customer existingCustomer=Customer.retrieve(custid);
		if(existingCustomer!=null) {
			 Map<String,Object> updateParam=new HashMap<String ,Object>();
			  updateParam.put("name",data.getName());
			  updateParam.put("phone",data.getPhone()); //updating a customer
			  existingCustomer=existingCustomer.update(updateParam);
			  return new CustomerData(existingCustomer.getName(),existingCustomer.getEmail(),existingCustomer.getId(),existingCustomer.getPhone());
		}
		else
			throw new RuntimeException("No customer fond with id "+custid);
	}
	@DeleteMapping("/customer/{custid}")
	public String deleteCustomer(@PathVariable String custid) throws StripeException
	{	Stripe.apiKey=stripeKey; 
		 Customer existingCustomer=Customer.retrieve(custid);
		 if(existingCustomer!=null) {
		  existingCustomer.delete(); 
		  return "Record delted";
		 }
		 else {
throw new RuntimeException("No customer fond with id "+custid);
		 }
		 
	}
	

}
