package com.riseup.flimbit.gateway.pan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.riseup.flimbit.request.PanRequest;

import java.util.Map;

@Service
public class KarzaPanVerificationService implements PanVerificationService {

    @Value("${default_active_pan_gateway}")
    String defaultProvider;
    

  //api.karza.in/v2/pan/otp/verify

    @Override
    public PanVerificationResponse initiateVerification(PanRequest panRequest) {
	  
	  
	  //@Value("${karza.api.key}")
	     String apiKey= "";

	    //@Value("${karza.api.url.initiate}")
	     String initiateUrl= "";   // Example: https://api.karza.in/v2/pan/otp/initiate

	   // @Value("${karza.api.url.verify}")
	     String verifyOtpUrl= "";  // Example: https
	  
	     RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-karza-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = Map.of(
                
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(initiateUrl, request, Map.class);

            PanVerificationResponse result = new PanVerificationResponse();
            result.setStatus("OTP_SENT");
            result.setTransactionId((String) response.getBody().get("transactionId"));
            result.setMessage("OTP sent to Aadhaar-linked mobile for PAN verification.");
            return result;

        } catch (Exception e) {
            PanVerificationResponse errorResponse = new PanVerificationResponse();
            errorResponse.setStatus("FAILED");
            errorResponse.setMessage("Failed to initiate PAN verification: " + e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public PanVerificationResponse verifyOtp(String transactionId, String otp) {
        HttpHeaders headers = new HttpHeaders();
  	  
  	  //@Value("${karza.api.key}")
  	     String apiKey= "";
  	     
	     RestTemplate restTemplate = new RestTemplate();


  	    //@Value("${karza.api.url.initiate}")
  	     String initiateUrl= "";   // Example: https://api.karza.in/v2/pan/otp/initiate

  	   // @Value("${karza.api.url.verify}")
  	     String verifyOtpUrl= "";  // Example: https
	  
        headers.set("x-karza-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = Map.of(
                "transactionId", transactionId,
                "otp", otp
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(verifyOtpUrl, request, Map.class);

            PanVerificationResponse result = new PanVerificationResponse();
            result.setStatus((String) response.getBody().getOrDefault("status", "FAILED"));
            result.setFullName((String) response.getBody().get("fullName"));
            result.setMessage("PAN verification completed successfully.");
            return result;

        } catch (Exception e) {
            PanVerificationResponse errorResponse = new PanVerificationResponse();
            errorResponse.setStatus("FAILED");
            errorResponse.setMessage("Failed to verify PAN OTP: " + e.getMessage());
            return errorResponse;
        }
    }
}
