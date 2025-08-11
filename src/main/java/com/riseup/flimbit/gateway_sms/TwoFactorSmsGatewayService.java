package com.riseup.flimbit.gateway_sms;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riseup.flimbit.constant.EntityName;

import com.riseup.flimbit.service.SystemSettingsService;

@Service
public class TwoFactorSmsGatewayService implements SmsGatewayService {
	
	Logger logger = LoggerFactory.getLogger(TwoFactorSmsGatewayService.class);

	  @Autowired
	    private SystemSettingsService systemSettingsService;

    @Value("${default_active_sms_gateway}")
    String defaultProvider;
	
	 private  static final String API_KEY = "YOUR_2FACTOR_API_KEY"; // Move to application.properties
	    private  final String BASE_URL = "https://2factor.in/API/V1/";
	    private final  String otpTemplateName = "";
	    
	

    @Override
    public OtpResponse sendOtp(String phoneNumber, String otp) {
    	 String activeProvider = Objects.requireNonNullElse(
    		        systemSettingsService.getValue("active_sms_gateway", EntityName.SMS.name()),
    		        defaultProvider
    		);		
    	 logger.info("2Factor send otp starrs for  phoneNumber " +  phoneNumber);
 
    	 logger.info(" Default Active provider "  + activeProvider);
    	 
    	 
    	 
    	 String  factorUrl = Objects.requireNonNullElse(
 		        systemSettingsService.getValue( activeProvider + "_sms_gateway_otp_url", EntityName.SMS.name()),
 		        "url Not found"
 	    	);		
    	 
    	 logger.info(" Default url "  + factorUrl);
    	 
    	 
    	 String  accessKey = Objects.requireNonNullElse(
  		        systemSettingsService.getValue( activeProvider + "_" +"sms_gateway_key", EntityName.SMS.name()),
  		        "accessKey Not found"
  	    	);		
     	 
     	 logger.info(" accessKey "  + accessKey);
     	 
     	 
     	 String  tempplateName = Objects.requireNonNullElse(
   		        systemSettingsService.getValue( activeProvider + "_" +"otp_template_name", EntityName.SMS.name()),
   		        "tempplateName Not found"
   	    	);		
      	 
      	 logger.info(" tempplateName "  + tempplateName);
     	 
     	 
     	 
    	 
      	 
      	String url = factorUrl
                .replace("{api_key}", accessKey)
                .replace("{phone_number}", phoneNumber)
                .replace("{otp_template_name}", tempplateName);
		 
      	 logger.info(" final url "  + url);
		 
    	
    	 //String url = BASE_URL + API_KEY + "/SMS/" + phoneNumber + "/AUTOGEN2/" + otpTemplateName;

         RestTemplate restTemplate = new RestTemplate();

         Map<String, Object> response = restTemplate.getForObject(url, Map.class);

         if (response != null && "Success".equalsIgnoreCase((String) response.get("Status"))) {
             return new OtpResponse("success", (String) response.get("Details"), "OTP sent successfully");
         } else {
             return new OtpResponse("failed", null, "Failed to send OTP");
         }  
         
    }

    @Override
    public OtpResponse verifyOtp(String sessionId, String otp) {
    	
    	String activeProvider = Objects.requireNonNullElse(
		        systemSettingsService.getValue("active_sms_gateway", EntityName.SMS.name()),
		        defaultProvider
		);		
	 logger.info("2Factor verifyOtp starrs for  sessionId " +  sessionId  + " :otp " +  otp);

	 logger.info(" Default Active provider "  + activeProvider);
	 
	 
	 
	 String  factorUrl = Objects.requireNonNullElse(
		        systemSettingsService.getValue( activeProvider + "_"+"sms_gateway_otp_verify", EntityName.SMS.name()),
		        "url Not found"
	    	);		
	 
	 logger.info(" Default url "  + factorUrl);
	 
	 
	 String  accessKey = Objects.requireNonNullElse(
		        systemSettingsService.getValue( activeProvider + "_" +"sms_gateway_key", EntityName.SMS.name()),
		        "accessKey Not found"
	    	);		
 	 
 	 logger.info(" accessKey "  + accessKey);
 	 
 	String url = factorUrl
            .replace("{api_key}", accessKey)
            .replace("{otp_session_id}", sessionId)
            .replace("{otp}", otp);

    RestTemplate restTemplate = new RestTemplate();

 	
 	Map<String, Object> response = restTemplate.getForObject(url, Map.class);
 	System.out.println("Response Map: " + response);

      	 
    if (response != null && "Success".equalsIgnoreCase((String) response.get("Status"))) {
        return new OtpResponse("success", (String) response.get("Details"), "OTP verified successfully");
    } else {
        return new OtpResponse("failed", null, "Failed to send OTP");
    }  
    }
}

