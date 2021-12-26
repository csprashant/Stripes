package com.nt.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.ChargeCollection;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceCollection;
import com.stripe.model.PaymentSource;
import com.stripe.model.PaymentSourceCollection;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItemCollection;
import com.stripe.model.SubscriptionSchedule;
import com.stripe.model.Token;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionScheduleCreateParams;
import com.stripe.param.SubscriptionUpdateParams;
@RestController
@RequestMapping("/api")
public class StripeControllerApi {
	Logger LOGGER = LoggerFactory.getLogger(StripeControllerApi.class);
	@Value("${stripe.apikey}")
	String stripeKey;
	@PostMapping("/customer")
	public CustomerData createCustomer(@RequestBody CustomerData data) throws StripeException {
		Stripe.apiKey=stripeKey; 				
		CustomerCreateParams params=CustomerCreateParams.builder()
				.setEmail(data.getEmail())
				.setName(data.getName())
				.setPhone(data.getPhone())
				.build();
		  Customer customer=Customer.create(params);
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
	public String addCardToCustomer(@PathVariable String custid/*,@RequestBody CardData data*/ )  throws StripeException {
		Stripe.apiKey=stripeKey; 
		System.out.println(custid);
		
		Map<String, Object> retrieveParams =new HashMap<>();
		List<String> expandList = new ArrayList<String>();
		expandList.add("sources");
		retrieveParams.put("expand", expandList);
		Customer existingCustomer=Customer.retrieve(custid,retrieveParams, null);
		if(existingCustomer!=null) {
			
		Map<String ,Object> cardParam=new HashMap<String,Object>();
		//cardParam.put("number", data.getNumber());
		cardParam.put("number","4000000000009995");
		cardParam.put("exp_month",02);
		cardParam.put("exp_year", 24);
		cardParam.put("cvc", 123);
		
		Map<String ,Object> tokenParam=new HashMap<String,Object>();
		tokenParam.put("card", cardParam);
		
		Token token=Token.create(tokenParam);
		
		Map<String,Object> source=new HashMap<String,Object>();
		source.put("source",token.getId());
		
		Card card = (Card)existingCustomer.getSources().create(source);
		//data.setId(card.getId());
		return "card created with id "+card.getId();
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
	/*
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
		@PostMapping("/charge/{custId}")
	public String chargeMutltipleTimesUsingCustomerID(@PathVariable String custId,@RequestBody ChargeData data) throws StripeException{
		Stripe.apiKey=stripeKey;
		Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", data.getAmount()*100);
        chargeParams.put("currency", data.getCurrency());
        chargeParams.put("customer", custId);
        chargeParams.put("capture",data.getCaptured());
        Charge charge = Charge.create(chargeParams);
        return charge.getId();
	}*/
	// creating charge using token 
	/*@PostMapping("charge/{cardId}")
	public String creatingChargeUsingToken(@PathVariable String cardId ,@RequestBody ChargeData data) throws StripeException{
		Stripe.apiKey=stripeKey;
		Map<String ,Object> cardParam=new HashMap<String ,Object>();
		cardParam.put("number", "5555555555554444");
		cardParam.put("exp_month","2");
		cardParam.put("exp_year","2025");
		Map<String ,Object> tokenparam=new HashMap<String,Object>();
		tokenparam.put("card",cardParam);
		Token token=Token.create(tokenparam);
		Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", data.getAmount()*100);
        chargeParams.put("currency", data.getCurrency());
        chargeParams.put("source",token.getId());
        chargeParams.put("capture",data.getCaptured());
        Charge charge=Charge.create(chargeParams);
        return charge.getId();		
	}*/
	
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
	
	@GetMapping("/charges")
	public List<Charge> getAllCharges() throws StripeException {
		Stripe.apiKey=stripeKey;
		Map<String, Object> params = new HashMap<>();
		params.put("limit", 3);
		ChargeCollection charges = Charge.list(params);
		List<Charge> data = charges.getData();
		return data;
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
	@PostMapping("/product")
	public String createProduct() throws StripeException {
		Stripe.apiKey=stripeKey;
		Map<String ,Object> prodParam=new HashMap<String,Object>();
		prodParam.put("name","Platinum plan");
		prodParam.put("type","service");
		Product product = Product.create(prodParam);
		System.out.println(product.getId());
	
		return product.getId();
		
	}
	@PostMapping("/plan")
	public String createPricingPlan() throws StripeException {
		Stripe.apiKey=stripeKey;
		Map<String ,Object> planParam=new HashMap<String,Object>();
		planParam.put("unit_amount",75*100);
		planParam.put("interval","month");
		planParam.put("currency","usd");
		planParam.put("product","prod_KR1VKm3tKxfB30");
		Price plan = Price.create(planParam);
		System.out.println(plan.getId());
		return plan.getId();
	}
	@PostMapping("/subscription/{custId}")
	public String createSubscription(@PathVariable String custId) throws StripeException {
		Stripe.apiKey=stripeKey;
		
		/*List<Object> items = new ArrayList<>();
		Map<String, Object> item1 = new HashMap<>();
		//item1.put("price",createPricingPlan());
		item1.put("price","price_1JmVynK6UiwvkuM0clmlEE0V");
		items.add(item1);
		Map<String, Object> params = new HashMap<>();
		params.put("customer",custId);
		params.put("items", items);
		*/
		SubscriptionCreateParams params=SubscriptionCreateParams.builder()
				.setCustomer(custId)
				.setProrationBehavior(SubscriptionCreateParams.ProrationBehavior.CREATE_PRORATIONS)
				.addItem(SubscriptionCreateParams.Item.builder().setPrice("price_1JpnJsK6UiwvkuM0VVW1fNS7").build())
				.build();
				
				
		//params.put("payment_behavior","default_incomplete");
	
		Subscription subscription =Subscription.create(params);
		SubscriptionItemCollection items2 = subscription.getItems();
		System.out.println("--------->"+items2.getData().get(0).getPrice().getRecurring().getInterval());
		System.out.println("--------->"+(items2.getData().get(0).getPrice().getUnitAmount())/100);
		System.out.println("--------->"+subscription.getMetadata());
		String latestInvoiceObject = subscription.getLatestInvoice();
		//System.out.println(latestInvoiceObject);
		
		
		/*	long val = subscription.getStartDate();
	 	Date date=new Timestamp(new Date(val*1000).getTime());
	 	System.out.println(date);*/
		long val = subscription.getCurrentPeriodStart();
		 		Date date=new Timestamp(new Date(val*1000).getTime());
	 			//System.out.println(date);
	    
		 
		 long val1=subscription.getCurrentPeriodEnd();
		 		Date date1=new Timestamp(new Date(val1*1000).getTime());
	 		//	System.out.println("End period-->"+date1);
		 
		 long val2=subscription.getBillingCycleAnchor();        
	        
	      	Date date2=new Timestamp(new Date(val2*1000).getTime());
	 		//System.out.println("Renew date--->"+date2);
	 	//	System.out.println("Due date "+subscription.getNextPendingInvoiceItemInvoice());
	 		
	 		
	        
	
		
		//System.out.println(subscription.getId());
		return subscription.toString();
	}

	
	@PutMapping("/subscription/{custId}")
	public String updateSubscription(@PathVariable String custId) throws StripeException {
		Stripe.apiKey=stripeKey;
		Subscription subscription =Subscription.retrieve("sub_1K3blXK6UiwvkuM0rlhbLeWO");
		System.out.println("subscription milla"+subscription.getCurrentPeriodEnd());
		Map<String, Object> param = new HashMap<>();
		param.put("cancel_at_period_end", true);
		subscription.update(param);
		
		/*Subscription subscription =Subscription.retrieve("sub_1JmGXdK6UiwvkuM0nbtpZFPm");
		SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
				.setCancelAtPeriodEnd(true)
				.build();
		subscription.update(params);*/
		System.out.println(subscription.getBillingCycleAnchor());
		SubscriptionScheduleCreateParams params =
				  SubscriptionScheduleCreateParams.builder()
				    .setCustomer(custId)
				    .setStartDate(subscription.getCurrentPeriodEnd())
				    .setEndBehavior(SubscriptionScheduleCreateParams.EndBehavior.RELEASE)
				    .addPhase(
				      SubscriptionScheduleCreateParams.Phase.builder()
				        .addItem(
				          SubscriptionScheduleCreateParams.Phase.Item.builder()
				            .setPrice("price_1JpnJsK6UiwvkuM0VVW1fNS7")
				            .setQuantity(1L)
				            .build())
				        .build())
				    .build();

				SubscriptionSchedule schedule = SubscriptionSchedule.create(params);
				return schedule.getSubscription();
		
	}
	@GetMapping("/cards/{custId}")
	public void deleteCardAPi(@PathVariable String custId) throws StripeException {
		Stripe.apiKey=stripeKey;
		Map<String, Object> retrieveParams = new HashMap<>();
		List<String> expandList = new ArrayList<>();
		expandList.add("sources");
		retrieveParams.put("expand", expandList);
		Customer customer = Customer.retrieve(custId, retrieveParams, null);
		if(customer!=null) {
			PaymentSourceCollection sources = customer.getSources();
			List<PaymentSource> data = sources.getData();
			Card card=null;
			for(int i=0;i<data.size();i++)
			{ card=(Card)data.get(i);
			System.out.println(card.getId());
			if(card.getId().equals("card_1JhtYMSBMy5RKIBwQMGZWeqj")) {
				//card.delete();
				System.out.println("card deleted"+card.getId());
			}
			}
		}
			
		}

		@GetMapping("/invoice/{invoiceId}")
		public void getInvoice(@PathVariable String invoiceId) throws StripeException {
			Stripe.apiKey=stripeKey;
			Invoice invoice = Invoice.retrieve(invoiceId);
			System.out.println(invoice.getNumber());
			System.out.println(invoice.getCustomer());
			System.out.println(invoice.getNumber());
			System.out.println(invoice.getStatus());
	}
		
		@PutMapping("/invoice/{invoiceId}")
		public void updateInvoice(@PathVariable String invoiceId) throws StripeException {
			Stripe.apiKey=stripeKey;
			Invoice invoice = Invoice.retrieve(invoiceId);
			Invoice pay = invoice.pay();
			System.out.println(pay.getStatus());
			System.out.println(pay.getNumber());
			
	}
		
		@PostMapping("charge/{invoiceId}")
		public String chargeAndCaputureForUnpaidInvoice(@PathVariable String invoiceId) throws StripeException{
			System.out.println("Exceuted");
			Stripe.apiKey=stripeKey;
			/*Map<String ,Object> cardParam=new HashMap<String ,Object>();
			cardParam.put("number", "5555555555554444");
			cardParam.put("exp_month","2");
			cardParam.put("exp_year","2025");
			Map<String ,Object> tokenparam=new HashMap<String,Object>();
			tokenparam.put("card",cardParam);
			Token token=Token.create(tokenparam);
			Map<String, Object> chargeParams = new HashMap<String, Object>();
			Invoice invoice = Invoice.retrieve(invoiceId);
	        chargeParams.put("amount", invoice.getAmountDue());
	        chargeParams.put("currency", invoice.getCurrency());
	        chargeParams.put("source",token.getId());
	        chargeParams.put("capture",true);
	        if(invoice.getStatus().equals("void")) {
	        	throw new RuntimeException("cannot not charge ");
	        }
	        Charge charge=Charge.create(chargeParams);
	        if(  charge.getStatus().equals("succeeded"))
	        {	InvoicePayParams param = InvoicePayParams.builder().setPaidOutOfBand(true).build();
	        	Invoice pay = invoice.pay(param);
	        	System.out.println("----->"+pay.getStatus());
	        }
	        return charge.getId();	*/	
			Invoice invoice = Invoice.retrieve(invoiceId);
			System.out.println(invoice.getDefaultSource());
			Invoice pay = invoice.pay();
		
			return pay.getStatus();
			
		}
		@GetMapping("/date")
		public String get() {
			DateTime dateTime = new DateTime();
			System.out.println(dateTime);
			DateTime plusMonths = dateTime.plusMonths(1);
			System.out.println("Plus month:"+plusMonths);
			DateTime plusYears = dateTime.plusYears(1);
			System.out.println(plusYears);
			return "Current date "+dateTime+"Plus month "+plusMonths+"plus year "+plusYears;
		}
		@PostMapping("/sechedule/{custId}")
		public void createSubscriptionsechdeule(@PathVariable String custId) throws StripeException {
			Stripe.apiKey=stripeKey;
			Subscription subscription =Subscription.retrieve("sub_1K58oWK6UiwvkuM0QoVfqQ2G");
			SubscriptionUpdateParams sparams = SubscriptionUpdateParams.builder()
					.setCancelAtPeriodEnd(true)
					//.setCancelAt(1639059000L)
					.build();
			
			subscription.update(sparams);
			SubscriptionScheduleCreateParams params =
					  SubscriptionScheduleCreateParams.builder()
					    .setCustomer(custId)
					    .setStartDate(subscription.getCurrentPeriodEnd()+600L)
					    .setEndBehavior(SubscriptionScheduleCreateParams.EndBehavior.RELEASE)
					    .addPhase(
					      SubscriptionScheduleCreateParams.Phase.builder()
					        .addItem(
					          SubscriptionScheduleCreateParams.Phase.Item.builder()
					            .setPrice("price_1JpnJsK6UiwvkuM0VVW1fNS7")
					            .setQuantity(1L)
					            .build())
					        .setProrationBehavior(SubscriptionScheduleCreateParams.Phase.ProrationBehavior.NONE)
					        .build())
					    .build();

					SubscriptionSchedule schedule = SubscriptionSchedule.create(params);
					System.out.println(schedule.getPhases().get(0).getStartDate());
					System.out.println(schedule.getStatus());
					System.out.println(schedule.getCustomer());
					System.out.println(schedule.getCreated());
					System.out.println(schedule.toString());
					//System.out.println(schedule.getSubscriptionObject().getItems().getData().get(0).getPrice().getRecurring().getInterval());
		}
		@GetMapping("/invoices")
		public void deleteSubscriptionAndvoidingAllInvoices() throws StripeException{
			Stripe.apiKey=stripeKey;
			Map<String, Object> sparams = new HashMap<>();
			sparams.put("subscription", "sub_1K3y4iK6UiwvkuM0wz1YYkIq");
			InvoiceCollection invoices = Invoice.list(sparams);
			List<Invoice> data = invoices.getData();
			for(Invoice invoice:data) {
				if(invoice.getStatus().equals("open") || invoice.getStatus().equals("draft")) {
					invoice.markUncollectible();
				}
			}
			Subscription subscription = Subscription.retrieve("sub_1K3y4iK6UiwvkuM0wz1YYkIq");
			subscription.cancel();
			System.out.println("Subscription cancelled");
		}
		@PostMapping("/status/{invoiceId}")
		public void changeStatus(@PathVariable String invoiceId) throws StripeException {
			Stripe.apiKey=stripeKey;
			Invoice invoice = Invoice.retrieve(invoiceId);
			System.out.println(invoice.getStatus());
			Invoice markUncollectible = invoice.markUncollectible();
			System.out.println(markUncollectible.getStatus());
			System.out.println(invoice.getStatus());
		}
		
		@PostMapping("/webhook")
		public String stripeWebhookEndpoint(@RequestBody String json, HttpServletRequest request,HttpServletResponse response) throws StripeException{         
			Stripe.apiKey=stripeKey;
			String header = request.getHeader("Stripe-Signature");      
	        String endpointSecret = "whsec_hukcnNrTUKazp6H4Pa0vADFI5UqqfyL1";
	        Event event=null;
	        try {
	          System.out.println(json);
	            System.out.println("=============================================");
	          //  System.out.println(header);
	           // System.out.println("*******************************************");
	        	event = Webhook.constructEvent(json, header, endpointSecret);
	            //System.out.println("Event.tosring"+event.toString());
	            // System.out.println("===========================");
	             //System.out.println("eevent object"+event);
	            // System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
	        
	            
	            //System.out.println("event.getType()"+event.getType()+"\n");          
	            
	        } catch (SignatureVerificationException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        // Deserialize the nested object inside the event
            EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;
            if (dataObjectDeserializer.getObject().isPresent()) {
                stripeObject = dataObjectDeserializer.getObject().get();
            } else {
                // Deserialization failed, probably due to an API version mismatch.
                // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
                // instructions on how to handle this case, or return an error here.
            }
            // Handle the event
            switch (event.getType()) {
                case "customer.subscription.created":
                	Subscription subscription=(Subscription)stripeObject;
                	/*//System.out.println("===>"+subscription.getId());
                	//String customer = subscription.getCustomer();
                	String latestInvoice = subscription.getLatestInvoice();
                	Invoice i=Invoice.retrieve(latestInvoice);
                	System.out.println("Description=="+i.getDefaultSource());
                	System.out.println("i.getAccountCountry()=="+i.getAccountCountry());
                	System.out.println("i.getBillingReason()=="+i.getBillingReason());
                	System.out.println("i.getCustomerName()=="+i.getCustomerName());
                	System.out.println("i.getCustomerEmail()=="+i.getCustomerEmail());
                	System.out.println("i.getNumber()=="+i.getNumber());
                	System.out.println("i.getStatus()=="+i.getStatus());
                	System.out.println("i.getAmountPaid()=="+i.getAmountPaid());
                	System.out.println("i.getAmountDue()=="+i.getAmountDue());
                	System.out.println("i.getAmountRemaining()=="+i.getAmountRemaining());
                	System.out.println("i.getPeriodStart()=="+i.getPeriodStart());
                	System.out.println("i.getPeriodEnd()=="+i.getPeriodEnd());
                	System.out.println("i.getTotal()=="+i.getTotal());
                	System.out.println("getCollectionMethod=="+i.getCollectionMethod());
                	System.out.println("getInvoicePdf=="+i.getInvoicePdf());
                	System.out.println(i.getHostedInvoiceUrl());
                	InvoiceLineItemCollection lines = i.getLines();
                	List<InvoiceLineItem> data = lines.getData();
                	InvoiceLineItem invoiceLineItem = data.get(0);
                	System.out.println(invoiceLineItem.getPeriod().getStart());
                	System.out.println(invoiceLineItem.getPeriod().getEnd());

                	*/
                
                    break;
              case "invoice.paid":
            	  
                	/*Invoice invoice=(Invoice)stripeObject;
                	System.out.println("989898"+invoice);
                	System.out.println("09090");
                	String id = invoice.getId();
        
                	String subscriptionId = invoice.getSubscription();
                	System.out.println("===>"+subscriptionId);
                	System.out.println("invoice id--->"+id);
                	String customer2 = invoice.getCustomer();
                    System.out.println("invoice paid customer"+customer2);*/
            	  break;
              case "invoice.voided":{
            	  System.out.println("void");
            	  
                	break;
                	}
              case "invoice.payment_failed":
            	  System.out.println("invoice.payment_failed");
            	  Invoice invoice=(Invoice)stripeObject;
            	// Charge charge = Charge.retrieve(invoice.getCharge());
            	// System.out.println("--->"+charge.getFailureMessage());
            	  //System.out.println(invoice.getChargeObject().getFailureMessage());
            	 break;
            	 
              case "payment_intent.payment_failed": {
					/*
					 * System.out.println("payment_intent.payment_failed"); PaymentIntent pi =
					 * (PaymentIntent)stripeObject;
					 * System.out.println("Customer-->"+pi.getCustomer());
					 * System.out.println("Invoice--->"+pi.getInvoice());
					 * System.out.println("Cardid--->"+pi.getSource());
					 * System.out.println("Status--->"+pi.getStatus());
					 * System.out.println("Reson--->"+pi.getCancellationReason());
					 * System.out.println("Invoice--->"+pi.getLastPaymentError().getMessage());
					 */
                  break;
              }
              case "charge.failed": {
            	  Charge ci = (Charge)stripeObject;
            	  System.out.println( "charge.failed");
            	  System.out.println("Customer-->"+ci.getCustomer());
            	  System.out.println("Invoice--->"+ci.getInvoice());
            	  System.out.println("Cardid--->"+ci.getSource().getId());
            	  System.out.println("Status--->"+ci.getStatus());
            	  System.out.println("Reson--->"+ci.getFailureMessage());
            	  System.out.println("Invoice--->"+ci.getAmount());
            	  System.out.println("date-->"+ci.getCreated());
            	 System.out.println("--->"+ci.getPaymentMethodDetails().getCard().getLast4()+","+ci.getPaymentMethodDetails().getCard().getExpMonth()+"/"+ci.getPaymentMethodDetails().getCard().getExpYear());
            	// System.out.println("Last4--->"+ci.getPaymentMethodDetails().getCard().getExpMonth());
            	 //System.out.println("Last4--->"+ci.getPaymentMethodDetails().getCard().getExpYear());
                  
                  break;
              }
              case "charge.succeeded": {
            	  Charge ci = (Charge)stripeObject;
            	  System.out.println( "charge.succeeded");
            	  System.out.println("Customer-->"+ci.getCustomer());
            	  System.out.println("Invoice--->"+ci.getInvoice());
            	  System.out.println("Cardid--->"+ci.getSource().getId());
            	  System.out.println("Status--->"+ci.getStatus());
            	  System.out.println("Reson--->"+ci.getFailureMessage());
            	  System.out.println("Invoice--->"+ci.getAmount());
            	  System.out.println("date-->"+ci.getCreated());
            	 System.out.println("--->"+ci.getPaymentMethodDetails().getCard().getLast4()+","+ci.getPaymentMethodDetails().getCard().getExpMonth()+"/"+ci.getPaymentMethodDetails().getCard().getExpYear());
            	// System.out.println("Last4--->"+ci.getPaymentMethodDetails().getCard().getExpMonth());
            	 //System.out.println("Last4--->"+ci.getPaymentMethodDetails().getCard().getExpYear());
                  
                  break;
                }
              case "subscription_schedule.created":
            	  System.out.println("subscription_schedule.created");
            	  SubscriptionSchedule subscriptionSchedule=(SubscriptionSchedule)stripeObject;
            		System.out.println(subscriptionSchedule.getPhases().get(0).getStartDate());
					System.out.println(subscriptionSchedule.getStatus());
					System.out.println(subscriptionSchedule.getCustomer());
					System.out.println(subscriptionSchedule.getCreated());
            	  break;
                
            	 
                default:
                   // System.out.println("Unhandled event type: " + event.getType());
                break;
            }
            return "";
            
	}
	
	
}