package com.nt.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.websocket.AsyncChannelGroupUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nt.model.CardData;
import com.nt.model.ChargeData;
import com.nt.model.CustomerData;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Token;
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
			 updateParam.put("email",data.getEmail());
			  updateParam.put("name",data.getName());
			  updateParam.put("phone",data.getPhone());//updating a customer
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
		  return "Customer delted";
		 }
		 else {
			 	throw new RuntimeException("No customer fond with id "+custid);
		 }
		 
	}
	@PostMapping("/card/{custid}")
	public String addCardToCustomer(@PathVariable String custid,@RequestBody CardData data )  throws StripeException {
		Stripe.apiKey=stripeKey; 
		System.out.println(custid);
		
		Map<String, Object> retrieveParams =new HashMap<>();
		List<String> expandList = new ArrayList<String>();
		expandList.add("sources");
		retrieveParams.put("expand", expandList);
		Customer existingCustomer=Customer.retrieve(custid,retrieveParams, null);
		if(existingCustomer!=null) {
			
		Map<String ,Object> cardParam=new HashMap<String,Object>();
		cardParam.put("number", data.getNumber());
		cardParam.put("exp_month",data.getMonth());
		cardParam.put("exp_year", data.getYear());
		cardParam.put("cvc", data.getCvc());
		
		Map<String ,Object> tokenParam=new HashMap<String,Object>();
		tokenParam.put("card", cardParam);
		
		Token token=Token.create(tokenParam);
		
		Map<String,Object> source=new HashMap<String,Object>();
		source.put("source",token.getId());
		
		Card card = (Card)existingCustomer.getSources().create(source);
		data.setId(card.getId());
		return "card created with id "+data.getId();
	}
		else
			throw new RuntimeException("No customer fond with id "+custid);
	}
	
	@GetMapping("/card/{custId}/{cardId}")
	public CardData getCard(@PathVariable() String custId,@PathVariable String cardId) throws StripeException {
		System.out.println(custId+"   "+cardId);
		Stripe.apiKey=stripeKey;
		Map<String, Object> retrieveParams = new HashMap<>();
		List<String> expandList = new ArrayList<>();
		expandList.add("sources");
		retrieveParams.put("expand", expandList);
		Customer customer = Customer.retrieve(custId, retrieveParams, null);
		if(customer!=null) {
			Card card = (Card) customer.getSources().retrieve(cardId);
			if(card!=null) {
				CardData data=new CardData();
				data.setId(card.getId());
				data.setMonth(card.getExpMonth().toString());
				data.setYear(card.getExpYear().toString());
				data.setNumber(card.getLast4());
				return data;
			}	
			else
				throw new RuntimeException("no card found with id "+cardId);
	}
	else
		throw new RuntimeException("no Customer found with id "+custId);
	}
	
	@DeleteMapping("/card/{custId}/{cardId}")
	public String deleteCard(@PathVariable String custId,@PathVariable String cardId) throws StripeException {
		Stripe.apiKey=stripeKey;
		Map<String, Object> retrieveParams = new HashMap<>();
		List<String> expandList = new ArrayList<>();
		expandList.add("sources");
		retrieveParams.put("expand", expandList);
		Customer customer = Customer.retrieve(custId, retrieveParams, null);
		if(customer!=null) {
			Card card = (Card) customer.getSources().retrieve(cardId);
			if(card!=null) {
				Card delete = card.delete();
				return "Card Deleted with id"+delete.getId();
				}
			else
				throw new RuntimeException("Card not found with id"+cardId);
			}
		else
			throw new RuntimeException("Customer not found id "+custId);
	}
	
	@PostMapping("/charge/{custId}")
	public String createCharge(@PathVariable String custId,@RequestBody ChargeData data) throws StripeException{
		Stripe.apiKey=stripeKey;
		Map<String, Object> retrieveParams = new HashMap<>();
		List<String> expandList = new ArrayList<>();
		expandList.add("sources");
		retrieveParams.put("expand", expandList);
		Customer customer = Customer.retrieve(custId, retrieveParams, null);
		if(customer!=null) {
	        String sourceCard = customer.getDefaultSource();
	        Map<String, Object> chargeParams = new HashMap<String, Object>();
	        chargeParams.put("amount", data.getAmount()*100);
	        chargeParams.put("currency", data.getCurrency());
	        chargeParams.put("customer", custId);
	        chargeParams.put("source", sourceCard);
	        chargeParams.put("capture",data.getCaptured());
	        System.out.println(sourceCard);
	        Charge charge = Charge.create(chargeParams);
	        return charge.toString();
	    }	
	else
		throw new RuntimeException("Customer not found id "+custId);
	}
	@GetMapping("/charge/{chargeId}")
	public ChargeData getCharge(@PathVariable String chargeId) throws StripeException {
		Stripe.apiKey=stripeKey;
		Charge charge=Charge.retrieve(chargeId);
		if(charge!=null) {
			ChargeData data=new ChargeData(charge.getId(),charge.getAmount(),charge.getCaptured(),charge.getCurrency());
			return data;
		}
		else
			throw new RuntimeException("no Charge found with id "+chargeId);
	}
	@PostMapping("/capture/{chargeId}")
	public ChargeData captureCharge(@PathVariable String chargeId) throws StripeException {
		Stripe.apiKey=stripeKey;
		Charge existingCharge=Charge.retrieve(chargeId);
		if(existingCharge!=null) {
			Charge charge = existingCharge.capture();
			ChargeData data=new ChargeData(charge.getId(),charge.getAmount(),charge.getCaptured(),charge.getCurrency());
			return data;
		}
		else
			throw new RuntimeException("no Charge found with id "+chargeId);
	}
	
}
