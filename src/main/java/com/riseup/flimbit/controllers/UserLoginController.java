package com.riseup.flimbit.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.request.PhoneRegValidateRequest;
import com.riseup.flimbit.request.PhoneRegisterRequest;
import com.riseup.flimbit.request.RefreshTokenRequest;
import com.riseup.flimbit.request.UserRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.TokenResponse;
import com.riseup.flimbit.service.UserRegisterService;
import com.riseup.flimbit.utility.JwtService;


@RestController
public class UserLoginController {
	
	Logger logger
    = LoggerFactory.getLogger(UserLoginController.class);
	
	@Autowired
	UserRegisterService userRegisterService;
	
	@Autowired
	JwtService jwtService;
	@Value("${isValidateTokenEnable}")
    boolean isValidateTokenEnable;
	
	@GetMapping("/")
	public String getMsg()
	{
		return "FlimBit service is running";
	}
	
	@PostMapping(path= "/sendregotp", consumes = "application/json", produces = "application/json")
	ResponseEntity<Object> generatePhoneOtpForRegister(@RequestBody PhoneRegisterRequest phoneValidateRequest)
	{
		
		return new ResponseEntity<>(userRegisterService.generateRegPhoneOtp(phoneValidateRequest),HttpStatus.OK);

		
	}
	
	@PostMapping(path= "/validateRegOtp", consumes = "application/json", produces = "application/json")
	ResponseEntity<Object> validateRegOtpPhone(@RequestHeader(value="deviceId") String deviceId,@RequestBody PhoneRegValidateRequest phoneValidateRequest)
	{
		
		return new ResponseEntity<>(userRegisterService.validateRegPhoneOtp(phoneValidateRequest,deviceId),HttpStatus.OK);

		
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
	@PostMapping("/updateUser")
    public ResponseEntity<?> refresh(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody UserRequest userRequest)
    {
		logger.info("isValidateTokenEnable "+ isValidateTokenEnable);
		if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
		return ResponseEntity.status(HttpStatus.OK).body(userRegisterService.updateUser(userRequest,phoneNumber));

    }
    
	
	
	

}
