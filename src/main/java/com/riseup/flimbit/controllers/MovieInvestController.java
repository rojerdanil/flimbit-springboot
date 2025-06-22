package com.riseup.flimbit.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.request.MovieInvestRequest;
import com.riseup.flimbit.request.MovieRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.MovieInvestService;
import com.riseup.flimbit.utility.JwtService;

@RestController
@RequestMapping(value = "/movieInvest")
public class MovieInvestController {
	Logger logger
    = LoggerFactory.getLogger(MovieController.class);
	
	@Autowired
	MovieInvestService movieInvestService;
	
	@Value("${isValidateTokenEnable}")
    boolean isValidateTokenEnable;
	
	@Autowired
	JwtService jwtService;
	
	@PostMapping("/buyShare")
    public ResponseEntity<?> updateMovie(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody MovieInvestRequest movieInvestdReq)
    {
		if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
        return ResponseEntity.status(HttpStatus.OK).body(movieInvestService.getBuyShares(movieInvestdReq, phoneNumber));
    }
	
	

}
