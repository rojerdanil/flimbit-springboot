package com.riseup.flimbit.gateway.pan;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.gateway_sms.TwoFactorSmsGatewayService;
import com.riseup.flimbit.request.PanRequest;
import com.riseup.flimbit.service.SystemSettingsService;



@Service
public class SurePassPanVerificationService implements PanVerificationService {
	
	Logger logger = LoggerFactory.getLogger(SurePassPanVerificationService.class);


    @Value("${default_active_pan_gateway}")
    private String defaultProvider;

    @Autowired
    private SystemSettingsService systemSettingsService;

    private static final String INITIATE_URL = "https://api.sandbox.co.in/kyc/pan/verify";
    private static final String VERIFY_OTP_URL = "https://api.sandbox.co.in/kyc/pan/verify-otp";
    
    @Autowired
    SurePassAuthService  surePassAuthService;
    

    @Override
    public PanVerificationResponse initiateVerification(PanRequest panRequest) {
        logger.info("SurePass PAN verification starts for PAN: {}", panRequest.getPanNumber());


        String token =   surePassAuthService.getAccessToken();
        
        
        // ✅ Fetch active provider dynamically
        String activeProvider = Objects.requireNonNullElse(
                systemSettingsService.getValue("default_active_pan_gateway", EntityName.PAN.name()),
                defaultProvider
        );

        // ✅ Fetch API Key
        String apiKey = systemSettingsService.getValue(activeProvider + "_gateway_key", EntityName.PAN.name());
        if (apiKey == null) {
            throw new RuntimeException("API key not configured for " + activeProvider);
        }

        // ✅ Fetch Initiate URL
        String initiateUrl = systemSettingsService.getValue(activeProvider + "_gateway_initiate_url", EntityName.PAN.name());
        if (initiateUrl == null) {
            throw new RuntimeException("Gateway initiate URL not found for " + activeProvider);
        }

        logger.info("Provider: {}", activeProvider);
        logger.info("Using URL: {}", initiateUrl);

        // ✅ Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization",  token);
        headers.set("x-api-key", apiKey); // ✅ Sometimes required
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ✅ Prepare body
        Map<String, String> body = Map.of(
        	    "@entity", "in.co.sandbox.kyc.pan_verification.request",
        	    "pan", panRequest.getPanNumber(),
        	    "name_as_per_pan", panRequest.getNameAsPerPan(),
        	    "date_of_birth", panRequest.getDateOfBirth(),
        	    "consent", "y",
        	    "reason", "user onboarding"
        	);


        RestTemplate restTemplate = new RestTemplate();
        PanVerificationResponse result = new PanVerificationResponse();

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(initiateUrl, new HttpEntity<>(body, headers), Map.class);

            Map<String, Object> responseBody = response.getBody();
            logger.info("Sandbox PAN API Response: {}", responseBody);

            if (responseBody != null && responseBody.containsKey("transaction_id")) {
            	System.out.println("response "+responseBody);
            	  Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

            	    result.setStatus((String) data.get("status"));
            	    result.setTransactionId((String) responseBody.get("transaction_id"));
            	    result.setMessage("PAN verification completed successfully in Sandbox.");
            	    result.setFullName((String) data.get("pan"));
            	    result.setNameAsPerPanMatch((Boolean) data.get("name_as_per_pan_match"));
            	    result.setSetDobMatch((Boolean) data.get("date_of_birth_match"));
            	    
            	    boolean finalStatus = 
            	    	    ("valid".equalsIgnoreCase(result.getStatus())) &&
            	    	    result.isNameAsPerPanMatch() &&
            	    	    result.isSetDobMatch();           	   
            	   logger.info("is final valid " + finalStatus);
            	   result.setFinalStatus(finalStatus);
            	   
            	    
            	    
            } else {
                result.setStatus("FAILED");
                result.setMessage("No transaction ID returned from " + activeProvider);
            }
        } catch (Exception e) {
            logger.error("PAN API call failed: {}", e.getMessage());
            result.setStatus("FAILED");
            result.setMessage("Sandbox API request failed: " + e.getMessage());
        }

        return result;
    }
    
    
    @Override
    public PanVerificationResponse verifyOtp(String transactionId, String otp) {

    	String activeProvider = Objects.requireNonNullElse(
		        systemSettingsService.getValue("default_active_pan_gateway", EntityName.PAN.name()),
		        defaultProvider
		);
    	
        String apiKey = systemSettingsService.getValue("surepass_api_key", EntityName.PAN.name());
        if (apiKey == null) {
            throw new RuntimeException("API key not configured for SurePass");
        }
        
            
        
        
        String  verifyUrl = systemSettingsService.getValue(activeProvider+"_"+"gateway_verify_otp_url", EntityName.PAN.name());

    
        
        
        
        if (verifyUrl == null) {
            throw new RuntimeException("gateway_verify_otp_url  is not found");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "transaction_id", transactionId,
                "otp", otp
        );
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.postForEntity(VERIFY_OTP_URL, new HttpEntity<>(body, headers), Map.class);
        Map<String, Object> responseBody = response.getBody();

        PanVerificationResponse result = new PanVerificationResponse();
        if (responseBody != null && "VERIFIED".equalsIgnoreCase((String) responseBody.get("status"))) {
            result.setStatus("VERIFIED");
            result.setFullName((String) responseBody.get("full_name"));
            result.setMessage("Verification completed successfully.");
        } else {
            result.setStatus("FAILED");
            result.setMessage("Verification failed or invalid OTP.");
        }
        return result;
    }
}
