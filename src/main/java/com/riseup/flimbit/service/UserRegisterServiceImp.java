package com.riseup.flimbit.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.UserStatus;
import com.riseup.flimbit.entity.User;
import com.riseup.flimbit.repository.UserRepository;
import com.riseup.flimbit.repository.UserStatusRespository;
import com.riseup.flimbit.request.PhoneRegValidateRequest;
import com.riseup.flimbit.request.PhoneRegisterRequest;
import com.riseup.flimbit.request.RefreshTokenRequest;
import com.riseup.flimbit.request.UserRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.PhoneOtpResponse;
import com.riseup.flimbit.response.TokenResponse;
import com.riseup.flimbit.utility.CommonUtilty;
import com.riseup.flimbit.utility.JwtService;
@Service
public class UserRegisterServiceImp implements UserRegisterService{
	
	Logger logger
    = LoggerFactory.getLogger(UserRegisterServiceImp.class);
	
	private Map<String,PhoneOtpResponse>  phoneRegOtpCache= new HashMap<>();
	@Autowired
	UserRepository userRepository;
	
	@Autowired 
	UserStatusRespository userStatusRespository;
	
	@Autowired
	JwtService jwtService;
   
	@Override
	public CommonResponse generateRegPhoneOtp(PhoneRegisterRequest phoneRegRequest) {
		// TODO Auto-generated method stub
		Optional<User> existUser = userRepository.findByphoneNumber(phoneRegRequest.getPhoneNumber());
		if(existUser.isPresent())
		{
			return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.USER_FOUND).build();
		}
		String otpCode = String.valueOf(new Random().nextInt(900000) + 100000); 
		PhoneOtpResponse phoneOtpResponse = PhoneOtpResponse.builder().code(otpCode)
				                           .expiryTime(Instant.now()).build();
		phoneRegOtpCache.put(phoneRegRequest.getPhoneNumber(), phoneOtpResponse);
		// trigger sms service to send  otp
		return CommonResponse.builder().status(Messages.STATUS_SUCCESS).result(phoneOtpResponse).build();
		
		
	}

	@Override
	public CommonResponse validateRegPhoneOtp(PhoneRegValidateRequest phRegValiReq,String device_id) {
		// TODO Auto-generated method stub
		
		PhoneOtpResponse phoneOtpRes = phoneRegOtpCache.get(phRegValiReq.getPhoneNumber());
		if(phoneOtpRes == null)  return  CommonResponse.builder().status(Messages.STATUS_SUCCESS).message( Messages.REG_PHONE_NUMBER_NOT_FOUND).build();
		
		boolean isExpired = Duration.between(phoneOtpRes.getExpiryTime(), Instant.now()).toMinutes() > 5; 
		if(isExpired)
		{
			phoneRegOtpCache.remove(phRegValiReq.getPhoneNumber());
			return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message( Messages.REG_OTP_EXPIRY).build();
			
		}
		
		boolean isValid = phoneOtpRes.getCode().equals(phRegValiReq.getOtp());
		logger.info(phRegValiReq.getPhoneNumber() +":"+isValid + ": "+ phoneOtpRes.getCode()+ " "+ phRegValiReq.getOtp());

		if(isValid)
		{
			
			phoneRegOtpCache.remove(phRegValiReq.getPhoneNumber());
			String token = jwtService.generateToken(phRegValiReq.getPhoneNumber() + ":"+device_id,false);
			String refreshToken = jwtService.generateToken(phRegValiReq.getPhoneNumber() + ":"+device_id,true);
			User user = new User();
			user.setPhoneNumber(phRegValiReq.getPhoneNumber());
			user.setStatus(Messages.USER_ACTIVE);
			user.setAccessKey(token);
			int id = userRepository.save(user).getId();
			logger.info("id "+ id);
			UserStatus userStatus = new UserStatus();
			userStatus.setUserId(id);
			userStatus.setPhoneVerified(true);
			userStatusRespository.save(userStatus);
			TokenResponse tokenRes = TokenResponse.builder().accessToken(token)
					.refreshToken(refreshToken)
					.accessTokenExpiry(jwtService.extractExpiration(token).toInstant())
					.refreshTokenExpiry(jwtService.extractExpiration(refreshToken).toInstant())
					.build();
			return CommonResponse.builder().status(Messages.STATUS_SUCCESS).result(tokenRes).build();
					

		}
		
			
		return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message (Messages.REG_OTP_NOT_MATCH).build();		
		
	}

	@Override
	public CommonResponse updateUser(UserRequest userRequest,String phoneNumber) {
		// TODO Auto-generated method stub
		Optional<User> userOpt = userRepository.findByphoneNumber(phoneNumber);
		if(userOpt.isPresent())
		{
			User user = userOpt.get();
			UserStatus userStatus = userStatusRespository.findByuserId(user.getId()).orElseGet(() -> {
				UserStatus userStatusNew = new UserStatus();
				userStatusNew.setUserId(user.getId());
				return userStatusNew;
			});
			
			if(CommonUtilty.checkEmptyOrNull(userRequest.getFirstName()))
			{
				user.setFirstName(userRequest.getFirstName());
				if(userRequest.getUserStatusVerify().isNamesVerified())
					userStatus.setNamesVerified(true);
			}	
			if(CommonUtilty.checkEmptyOrNull(userRequest.getLastName()))
				{
				user.setLastName(userRequest.getLastName());
				}
			if(CommonUtilty.checkEmptyOrNull(userRequest.getEmail()))
			{
				user.setEmail(userRequest.getEmail());
				if(userRequest.getUserStatusVerify().isEmailVerified())
				userStatus.setEmailVerified(true);
			}
			if(CommonUtilty.checkEmptyOrNull(userRequest.getPanId()))
			{
				user.setPanId(userRequest.getPanId());
				if(userRequest.getUserStatusVerify().isPanVerified())
			    	userStatus.setPanVerified(true);
			}
			if(CommonUtilty.checkEmptyOrNull(userRequest.getStatus()))
			{
				user.setStatus(userRequest.getStatus());
			}
			if(CommonUtilty.checkEmptyOrNull(userRequest.getLanguage()))
			{
				user.setLanguage(userRequest.getLanguage());
				if(userRequest.getUserStatusVerify().isLanguageVerified())
					userStatus.setLanguageVerified(true);
			}
			
			userRepository.save(user);
			userStatusRespository.save(userStatus);
			return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_UPATE_SUCCESS).build();
		}
		
		 return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.REG_PHONE_NUMBER_NOT_FOUND).build();

	}

	@Override
	public CommonResponse genRefreshToken(String deviceId, String phoneNumber, RefreshTokenRequest refreshRequest) {
		// TODO Auto-generated method stub
		 String refreshToken = refreshRequest.getRefreshToken();
		 CommonResponse commonToken = jwtService.validateToken(refreshRequest.getRefreshToken(), deviceId, phoneNumber);

	        if (commonToken.getStatus() != Messages.SUCCESS) {
	        	return commonToken;
	        }
	        String token = jwtService.generateToken(phoneNumber + ":"+deviceId,false);
	        Optional<User> userOpt = userRepository.findByphoneNumber(phoneNumber);
	        if(userOpt.isPresent())
	        {
	        	User user = userOpt.get();
	        	user.setAccessKey(token);
	        	userRepository.save(user);
	        	TokenResponse tokenRes = TokenResponse.builder().accessToken(token)
						.refreshToken(refreshToken)
						.accessTokenExpiry(jwtService.extractExpiration(token).toInstant())
						.refreshTokenExpiry(jwtService.extractExpiration(refreshToken).toInstant())
						.build();
	        	
	        	return CommonResponse.builder().status(Messages.STATUS_SUCCESS).result(tokenRes).build();

					
	        }
	        else
	        	return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.REG_PHONE_NUMBER_NOT_FOUND).build();

	        
	        
	         

		
	}

}
