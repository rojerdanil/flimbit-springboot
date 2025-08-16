package com.riseup.flimbit.controllers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.dto.UserInvestmentSectionDTO;
import com.riseup.flimbit.entity.dto.UserWithStatusWebDto;
import com.riseup.flimbit.request.EmailRequest;
import com.riseup.flimbit.request.PanRequest;
import com.riseup.flimbit.request.PhoneRegValidateRequest;
import com.riseup.flimbit.request.PhoneRegisterRequest;
import com.riseup.flimbit.request.RefreshTokenRequest;
import com.riseup.flimbit.request.RegisterUserName;
import com.riseup.flimbit.request.StatusRequest;
import com.riseup.flimbit.request.UserRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.TokenResponse;
import com.riseup.flimbit.service.UserRegisterService;
import com.riseup.flimbit.utility.HttpResponseUtility;
import com.riseup.flimbit.utility.JwtService;


@RestController
@RequestMapping(value = "/users")
public class UserLoginController {
	
	Logger logger
    = LoggerFactory.getLogger(UserLoginController.class);
	
	@Autowired
	UserRegisterService userRegisterService;
	
	@GetMapping("/")
	public String getMsg()
	{
		return "FlimBit service is running";
	}
	
		@PostMapping("/mobile/updateUserName")
    public ResponseEntity<?> updateUser(@RequestHeader(value="X-Device-ID") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestBody RegisterUserName userRequest)
    {
		return HttpResponseUtility.getHttpSuccess(userRegisterService.updateUser(userRequest,phoneNumber));

    }
    
	
	@GetMapping("/dataTableUsers")
	public ResponseEntity<?> getPaginatedRewards(
			@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
	        @RequestParam int draw,
	        @RequestParam int start,
	        @RequestParam int length,
	        @RequestParam(required = false) String searchText,
	        @RequestParam(defaultValue = "id") String sortColumn,
	        @RequestParam(defaultValue = "asc") String sortOrder,
	        @RequestParam(required = false) String language,
	        @RequestParam(required = false) String movie,
	        @RequestParam(required = false) String status   
	) {
		
		
		
    	int languagex = language == null || language.isEmpty() ? 0 : Integer.parseInt(language);
    	int moviex = movie == null || movie.isEmpty() ? 0 : Integer.parseInt(movie);
    	status = status == null || status.isEmpty() ? null : status;
    	searchText = searchText == null || searchText.isEmpty() ? null : searchText;
       
    	

	    Page<UserWithStatusWebDto> page = userRegisterService.fetchAllUsersWithStatus
	    		( languagex,moviex,status,searchText,start, length,  sortColumn, sortOrder);
	    Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", page.getTotalElements());
        response.put("recordsFiltered", page.getTotalElements());
        response.put("data", page.getContent());
        return HttpResponseUtility.getHttpSuccess(response);
	}

	
	@PostMapping("/udpateStatus")
	public ResponseEntity<?> getPaginatedRewards(
			@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody StatusRequest statusRequest)
	        
    		{
		
		
		
	            return HttpResponseUtility.getHttpSuccess(userRegisterService.updateUserStatus(statusRequest));
	}
	@PostMapping(path= "/mobile/sendVerificationEmail", consumes = "application/json", produces = "application/json")
	ResponseEntity<?> sendVerfificationMail(@RequestHeader(value="X-Device-ID") String deviceId,@RequestBody EmailRequest emailRequest)
	{
		
		
		
	//	return new ResponseEntity<>(userRegisterService.validateRegPhoneOtp(phoneValidateRequest,deviceId),HttpStatus.OK);
		return HttpResponseUtility.getHttpSuccess(userRegisterService.sendVerificationEmail(emailRequest));		
	}
	
	@GetMapping(path= "/mobile/verifyEmail/{code}",  produces = "application/json")
	ResponseEntity<?> getVerifyMail(@PathVariable String code)
	{
		
	//	return new ResponseEntity<>(userRegisterService.validateRegPhoneOtp(phoneValidateRequest,deviceId),HttpStatus.OK);
		return HttpResponseUtility.getHttpSuccess(userRegisterService.verifyEmail(code));		
	}
	@GetMapping(path= "/mobile/verifyLanguage/{code}",  produces = "application/json")
	ResponseEntity<?> getVerifyLanguage(@PathVariable String code)
	{
		try {
			    long langId = Long.parseLong(code);
				return HttpResponseUtility.getHttpSuccess(userRegisterService.verifyLanguage(langId));		


		}catch (NumberFormatException e) {
	        throw new RuntimeException("Invalid long value: ");
	    }
		
	//	return new ResponseEntity<>(userRegisterService.validateRegPhoneOtp(phoneValidateRequest,deviceId),HttpStatus.OK);
	}
	
	
	@PostMapping(path= "/mobile/initiatePan", consumes = "application/json", produces = "application/json")
	ResponseEntity<?> sendPanInitiate(@RequestBody PanRequest panRequest)
	{
		
	//	return new ResponseEntity<>(userRegisterService.validateRegPhoneOtp(phoneValidateRequest,deviceId),HttpStatus.OK);
		return HttpResponseUtility.getHttpSuccess(userRegisterService.initiatePan(panRequest));		
	}
	
	@GetMapping(path= "/mobile/userProfile",  produces = "application/json")
	ResponseEntity<?> getUserProfileData()
	{
		
	return new ResponseEntity<>(userRegisterService.getUserProfileMobile(),HttpStatus.OK);
	}
	
	@PostMapping("/mobile/refresh")
	public ResponseEntity<?> refresh(@RequestHeader(value = "X-Device-ID") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber, @RequestBody RefreshTokenRequest refreshRequest) {
		CommonResponse commonToken = userRegisterService.genRefreshToken(deviceId, phoneNumber, refreshRequest);

		if (commonToken.getStatus() != Messages.SUCCESS) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
		}

		return ResponseEntity.status(HttpStatus.OK).body(commonToken);

	}
}
