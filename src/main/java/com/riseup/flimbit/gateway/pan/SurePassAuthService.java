package com.riseup.flimbit.gateway.pan;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.service.SystemSettingsService;

import java.time.Instant;
import java.util.Map;

@Service
public class SurePassAuthService {
	
    @Autowired
    private SystemSettingsService systemSettingsService;

    @Value("${SurePass_gateway_key}")
    private String accessKeyProp;

    @Value("${surepass.secret_key}")
    private String secretKeyProp;
    

    @Value("${SurePass_gateway_authenticate_url}")
    private String authUrlProp;

    private String cachedToken;
    private Instant tokenExpiryTime;


    Logger logger = LoggerFactory.getLogger(SurePassPanVerificationService.class);
    
    public String getAccessToken() {
        // ✅ Return cached token if still valid
        if (cachedToken != null && tokenExpiryTime != null && Instant.now().isBefore(tokenExpiryTime)) {
        	logger.info("catched token found ");
            return cachedToken;
        }
        
        String auth_url = systemSettingsService.getValue("SurePass_gateway_authenticate_url", EntityName.PAN.name());
        String  apiKey = systemSettingsService.getValue("SurePass_gateway_key", EntityName.PAN.name());
        String  secretKey = systemSettingsService.getValue("surepass.secret_key", EntityName.PAN.name());

		
        logger.info("******* SurePassAuthService starts ************** ");
        
        logger.info(" data  from system seting " + auth_url  + " :apiKey: " + apiKey + " : secrety key : " +secretKey);
        
        logger.info(" data  from  application seting " + authUrlProp  + " :apiKey: " + accessKeyProp + " : secrety key : " +secretKeyProp);

        
        
        auth_url  = auth_url != null ? auth_url : authUrlProp;
        apiKey  = apiKey != null ? apiKey : accessKeyProp;
        secretKey  = secretKey != null ? secretKey : secretKeyProp;
        
        logger.info(" data  from after  " + auth_url  + " :apiKey: " + apiKey + " : secrety key : " +secretKey);
        

        // ✅ Request new token
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("x-api-secret", secretKey);
        headers.set("x-api-version","");

        
        

        Map<String, String> requestBody = Map.of(
             
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(auth_url, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
        	
            Map<String, Object> body = response.getBody();

        	 Map<String, Object> data = (Map<String, Object>) body.get("data");
        	    if (data != null && data.containsKey("access_token")) {
        	        cachedToken = (String) data.get("access_token");
        	    } else {
        	        cachedToken = (String) body.get("access_token"); // fallback
        	    }

        	    Integer expiresIn = 86400; // default 24 hrs
        	    if (body.containsKey("expires_in")) {
        	        expiresIn = (Integer) body.get("expires_in");
        	    }

        	    tokenExpiryTime = Instant.now().plusSeconds(expiresIn - 300);
        } else {
            throw new RuntimeException("Failed to fetch SurePass access token");
        }

        return cachedToken;
    }
}
