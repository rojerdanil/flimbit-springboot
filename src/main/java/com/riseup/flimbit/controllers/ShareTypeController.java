package com.riseup.flimbit.controllers;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.MovieShareType;
import com.riseup.flimbit.request.ShareTypeRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.ShareTypeService;
import com.riseup.flimbit.utility.JwtService;

@RestController
@RequestMapping(value = "/share-types")
public class ShareTypeController {
	Logger logger
    = LoggerFactory.getLogger(ShareTypeController.class);
	
	@Autowired
	JwtService jwtService;
	@Value("${isValidateTokenEnable}")
    boolean isValidateTokenEnable;

	@Autowired
	 ShareTypeService service;

	@PostMapping(value = "/updateInsert/{type}")
	public ResponseEntity<?> create(
			@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@PathVariable String type,
			@RequestBody ShareTypeRequest shareType) {
		if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
		
		return ResponseEntity.ok(service.create(shareType,type));
	}

	@GetMapping("/deleteShareType/{id}")
	public ResponseEntity<?> delete(
			@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
			@PathVariable Long id) {
		
		

		if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
		service.delete(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
				.status(Messages.STATUS_SUCCESS)
				.message(Messages.STATUS_SUCCESS)
				.build()
				);

	}

	@GetMapping("/movie/{movieId}")
	public ResponseEntity<?> getByMovieId(
			@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@PathVariable Long movieId) {
		
		
		if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
		
		
		return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
				.status(Messages.STATUS_SUCCESS)
				.result(service.getShareTypeByMovieId(movieId)).build()
				);
	}
}
