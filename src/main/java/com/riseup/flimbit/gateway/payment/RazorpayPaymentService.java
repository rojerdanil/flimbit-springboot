package com.riseup.flimbit.gateway.payment;

import com.razorpay.*;
import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.service.SystemSettingsService;


import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@Service
public class RazorpayPaymentService implements PaymentService {


    @Value("${razorpay.key}") String keyProp;
    @Value("${razorpay.secret}") 
    String secretProp;
    
    @Autowired
    private SystemSettingsService systemSettingsService;

    Logger logger = LoggerFactory.getLogger(RazorpayPaymentService.class);

    
    public RazorpayClient getRazorPay() {
    	 
    	 
    	         try {
    	        	 String key = Objects.requireNonNullElse(
    	     	            systemSettingsService.getValue("razorpay_payment_gateway_api_key", EntityName.PAYMENT.name()),
    	     	            keyProp
    	     	        );
    	     	 
    	     	 String secret = Objects.requireNonNullElse(
    	  	            systemSettingsService.getValue("razorpay_payment_gateway_secret_key", EntityName.PAYMENT.name()),
    	  	           secretProp
    	  	        );
    	     	 
    	     	 logger.info("Razor key :" + systemSettingsService.getValue("razorpay_payment_gateway_api_key", EntityName.PAYMENT.name())
    	     	 + ": key value : "+  systemSettingsService.getValue("razorpay_payment_gateway_secret_key", EntityName.PAYMENT.name()) );
    	     	 
					return new RazorpayClient(key, secret);
				} catch (RazorpayException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
    }

    @Override
    public String createOrder(String orderId, BigDecimal amount, String currency,Map<String, Object> notes) throws Exception {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100))); // amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", orderId);
        orderRequest.put("payment_capture", 1);
         RazorpayClient client = getRazorPay();
         if (notes != null && !notes.isEmpty()) {
             JSONObject jsonNotes = new JSONObject(notes);
             orderRequest.put("notes", jsonNotes);
         }
         
         if(client == null)
        	 throw new RuntimeException();
        Order order = client.orders.create(orderRequest);
        return order.get("id");
    }

    @Override
    public boolean verifyPayment(String paymentId, String signature, String orderId) {
    	
    	 String secret = Objects.requireNonNullElse(
	  	            systemSettingsService.getValue("razorpay_payment_gateway_secret_key", EntityName.PAYMENT.name()),
	  	           secretProp
	  	        );

        try {
        	 String data = orderId + "|" + paymentId;
             return Utils.verifySignature(data, signature, secret);
        } catch (Exception e) {
            return false;
        }
    }

	@Override
	public PaymentOrderResponse getOrder(String gatewayOrderId) throws Exception {
		// TODO Auto-generated method stub
		 RazorpayClient client = getRazorPay();
		    if (client == null) {
		        throw new RuntimeException("Failed to initialize Razorpay client");
		    }
		    Order order = client.orders.fetch(gatewayOrderId);

		    return PaymentOrderResponse.builder()
		            .orderId(order.get("id"))
		            .internalOrderRef(order.get("receipt"))
		            .status(order.get("status"))
		            .amount(order.get("amount") != null ? new BigDecimal(order.get("amount").toString()).divide(BigDecimal.valueOf(100)) : BigDecimal.ZERO)
		            .amountPaid(order.get("amount_paid") != null ? new BigDecimal(order.get("amount_paid").toString()).divide(BigDecimal.valueOf(100)) : BigDecimal.ZERO)
		            .currency(order.get("currency"))
		            .provider("RAZORPAY")
		            .build();	}
}
