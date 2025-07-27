package com.riseup.flimbit.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.AdminUser;
import com.riseup.flimbit.request.LoginRequest;
import com.riseup.flimbit.request.PhoneRegValidateRequest;
import com.riseup.flimbit.request.PhoneRegisterRequest;
import com.riseup.flimbit.request.RefreshTokenRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.TokenResponse;
import com.riseup.flimbit.service.AdminUserService;
import com.riseup.flimbit.service.UserRegisterService;
import com.riseup.flimbit.utility.HttpResponseUtility;
import com.riseup.flimbit.utility.JwtService;

@RestController
@RequestMapping(value = "/login")
public class LoginController {
	Logger logger
    = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	UserRegisterService userRegisterService;
	@Autowired
	JwtService jwtService;
	
	@Autowired
	AdminUserService adminUserService;
	
	
	@PostMapping(path= "/web/login", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		TokenResponse tokenResponse = adminUserService.loginWithIdentifier(request);

	    if (tokenResponse != null) {
	    	        return HttpResponseUtility.getHttpSuccess(tokenResponse); // or generate token, etc.
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
	    }
	}
	
	
	
	@PostMapping(path= "/sendregotp", consumes = "application/json", produces = "application/json")
	ResponseEntity<Object> generatePhoneOtpForRegister(@RequestBody PhoneRegisterRequest phoneValidateRequest)
	{
		
		return new ResponseEntity<>(userRegisterService.generateRegPhoneOtp(phoneValidateRequest),HttpStatus.OK);

		
	}
	
	@PostMapping(path= "/validateRegOtp", consumes = "application/json", produces = "application/json")
	ResponseEntity<Object> validateRegOtpPhone(@RequestHeader(value="X-Device-ID") String deviceId,@RequestBody PhoneRegValidateRequest phoneValidateRequest)
	{
		
		return new ResponseEntity<>(userRegisterService.validateRegPhoneOtp(phoneValidateRequest,deviceId),HttpStatus.OK);

		
	}
	
	
	@PostMapping("/web/verify-token")
    public ResponseEntity<?> verifyToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader("X-Device-ID") String deviceId
    ) {
		CommonResponse response = null ;

        try {
            // Extract token from "Bearer <token>"

            // Extract username/email/device from token (depending on how you built it)

            // Call your custom validation method
            response =  adminUserService.validateWebToken(authHeader, deviceId);
            
            if(response == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
            else
            {
            
            if(response.getStatus() == Messages.STATUS_SUCCESS)
            	return HttpResponseUtility.getHttpSuccess(response);
            else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
            }
            	

        } catch (Exception ex) {
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
        }

    }
	

	
	@GetMapping("/web/refresh-token")
    public ResponseEntity<?> refreshToken(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader("X-Device-ID") String deviceId
    ) {
		CommonResponse response = null ;

        try {
            // Extract token from "Bearer <token>"

            // Extract username/email/device from token (depending on how you built it)

            // Call your custom validation method
            response =  adminUserService.validateRefreshWebToken(authHeader, deviceId);
            
            if(response == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
            else
            {
            
            if(response.getStatus() == Messages.STATUS_SUCCESS)
            	return HttpResponseUtility.getHttpSuccess(response);
            else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
            }
            	

        } catch (Exception ex) {
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
        }

    }
	
	
	
	
	
	@PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,@RequestBody RefreshTokenRequest refreshRequest) {
        CommonResponse commonToken = userRegisterService.genRefreshToken(deviceId, phoneNumber, refreshRequest);

        if (commonToken.getStatus() != Messages.SUCCESS) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
        }

		return ResponseEntity.status(HttpStatus.OK).body(commonToken);
		
       
        
    }

	
}
