package com.nt.StrilpesExamples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Customer;

@SpringBootApplication
public class StrilpesExamplesApplication {

	public static void main(String[] args) throws StripeException {
		SpringApplication.run(StrilpesExamplesApplication.class, args);
		Stripe.apiKey = "sk_test_51JevWOSBMy5RKIBwbEhUvPOctzya67QaHaVWm0vcewEtyY2OKCwoohzBgR3BufZVfm1U8uFR9AQhTQN58MW5n3A4009yYOBpEt";
		// creating a customer
		/*
		  Map<String ,Object> customerParam=new HashMap<String,Object>(); 
		  Map<String,Object> address=new HashMap<String,Object>(); 
		  address.put("city", "Bhilai");
		  address.put("country", "IN"); address.put("line1", "Sector II");
		  address.put("line2", "Near supela"); address.put("state", "Chhattisgrah");
		  
		  customerParam.put("email", "shubhambhal@gmail.com");
		  customerParam.put("name", "Shubham Bhal"); customerParam.put("phone",
		  "9696969696"); customerParam.put("address", address); Customer
		  newCustomer=Customer.create(customerParam);
		  System.out.println(newCustomer.getId());
		 */
		
		
		/*
		  // Retriving a customer and updating Customer
		  existingCustomer=Customer.retrieve("cus_KJYtC2BqnPV19q");
		   Gson gson=new GsonBuilder().setPrettyPrinting().create();
		  System.out.println("Before update "+gson.toJson(existingCustomer));
		  Map<String ,Object> address=new HashMap<String,Object>();
		   address.put("city","Bhilai");
		    address.put("country", "IN"); 
		    address.put("line1", "Sector II");
		  address.put("line2", "Near supela"); 
		  address.put("state", "Chhattisgrah");
		  Map<String,Object> dataParam=new HashMap<String ,Object>();
		  dataParam.put("address", address);
		  existingCustomer=existingCustomer.update(dataParam);
		  System.out.println(gson.toJson(gson.toJson(existingCustomer)));
		 */

		/*
		  Map<String,Object> updateParam=new HashMap<String ,Object>();
		  updateParam.put("name","Navin Sharma");
		  updateParam.put("phone","8987858584"); //updating a customer
		  existingCustomer.update(updateParam);
		  System.out.print("existing Customer:====>\n"+existingCustomer);
		 */
		
		/*
		  //deleting a customer // fist retrive the customer using customer id Customer
		  existingCustomer=Customer.retrieve("cus_KJcLHwGl6WrfZt");
		  existingCustomer.delete(); System.out.println("customer deleted");
		 */
		/*-------------------------------------------------------------------
		// addingcard to Customer
		Map<String, Object> retrieveParams =new HashMap<>();
		List<String> expandList = new ArrayList<>();
		expandList.add("sources");
		retrieveParams.put("expand", expandList);
		Customer existingCustomer =  Customer.retrieve( "cus_KJYtC2BqnPV19q",retrieveParams, null);
		System.out.print(existingCustomer);
		Map<String ,Object> cardParam=new HashMap<String,Object>();
		cardParam.put("number", "4000056655665556");
		cardParam.put("exp_month","8");
		cardParam.put("exp_year", "2025");
		cardParam.put("cvc", "155");
		
		Map<String ,Object> tokenParam=new HashMap<String,Object>();
		tokenParam.put("card", cardParam);
		
		Token token=Token.create(tokenParam);
		
		Map<String,Object> source=new HashMap<String,Object>();
		source.put("source",token.getId());
		
		Card card = (Card)existingCustomer.getSources().create(source);
		
		
		Gson gson1=new GsonBuilder().setPrettyPrinting().create();
		System.out.print(gson1.toJson(existingCustomer));
		
		------------------------------------------*/
		/*--------------------------------------------------------------------
				// retriving a card
		
				Map<String, Object> retrieveParams = new HashMap<>();
				List<String> expandList = new ArrayList<>();
				expandList.add("sources");
				retrieveParams.put("expand", expandList);
				Customer customer = Customer.retrieve("cus_KJYtC2BqnPV19q", retrieveParams, null);
		
				Card card = (Card) customer.getSources().retrieve("card_1Jf2QWSBMy5RKIBwSSka0UQl");
				System.out.println("--->"+card.getLast4());
				System.out.println("---->"+card.getExpMonth());
				System.out.println("---->"+card.getExpYear());
				System.out.println("---->"+card.toString());
		---------------------------------------------------------------------*/
		/*---------------------------------------------------------------------
		// updatig a card
		
		Map<String, Object> retrieveParams = new HashMap<>();
		List<String> expandList = new ArrayList<>();
		expandList.add("sources");
		retrieveParams.put("expand", expandList);
		Customer customer = Customer.retrieve("cus_KJYtC2BqnPV19q", retrieveParams, null);
		
		Card card = (Card)customer.getSources().retrieve("card_1Jf2QWSBMy5RKIBwSSka0UQl");
		
		Map<String, Object> params = new HashMap<>();
		params.put("name", "Prashant kumar puse");
		params.put("address_city", "Durg");
		params.put("address_country", "India");
		params.put("address_state", "Chhattisgrah");
		
		Card updatedCard = (Card) card.update(params);
		System.out.println("--->"+updatedCard.getLast4());
		System.out.println("---->"+updatedCard.getExpMonth());
		System.out.println("---->"+updatedCard.getExpYear());
		System.out.println("---->"+updatedCard.toString());
		System.out.println("--->"+updatedCard.getName());
		System.out.println("---->"+updatedCard.getAddressCity());
		System.out.println("---->"+updatedCard.getAddressCountry());
		System.out.println("---->"+updatedCard.toString());
		---------------------------------------------------------------------*/
		/*-----------------------------------------------------------------
		//Deleting a card
		Map<String, Object> retrieveParams = new HashMap<>();
		List<String> expandList = new ArrayList<>();
		expandList.add("sources");
		retrieveParams.put("expand", expandList);
		Customer customer = Customer.retrieve("cus_KJYtC2BqnPV19q", retrieveParams, null);
		Card card = (Card)customer.getSources().retrieve("card_1Jf2QWSBMy5RKIBwSSka0UQl");
		Card deletedCard = (Card) card.delete();
		System.out.println("Deleted card is "+deletedCard.getId());
		-----------------------------------------------------------------*/
	}
}
