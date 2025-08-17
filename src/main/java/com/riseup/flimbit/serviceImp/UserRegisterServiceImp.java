package com.riseup.flimbit.serviceImp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.management.RuntimeErrorException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riseup.flimbit.constant.ActionType;
import com.riseup.flimbit.constant.ConfigCacheService;
import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.constant.PromotionTypeEnum;
import com.riseup.flimbit.constant.StatusEnum;
import com.riseup.flimbit.entity.Language;
import com.riseup.flimbit.entity.PromoCode;
import com.riseup.flimbit.entity.PromotionType;
import com.riseup.flimbit.entity.ReferralTracking;
import com.riseup.flimbit.entity.User;
import com.riseup.flimbit.entity.UserPromoCode;
import com.riseup.flimbit.entity.UserStatus;
import com.riseup.flimbit.entity.dto.UserWithStatusWebDto;
import com.riseup.flimbit.gateway.email.EmailService;
import com.riseup.flimbit.gateway.pan.PanService;
import com.riseup.flimbit.gateway.pan.PanVerificationResponse;
import com.riseup.flimbit.gateway.pan.SurePassAuthService;
import com.riseup.flimbit.gateway_sms.OtpResponse;
import com.riseup.flimbit.gateway_sms.OtpService;
import com.riseup.flimbit.gateway_sms.TwoFactorResponse;
import com.riseup.flimbit.repository.LanguageRepository;
import com.riseup.flimbit.repository.PromoCodeRepository;
import com.riseup.flimbit.repository.PromotionTypeRepository;
import com.riseup.flimbit.repository.ReferralTrackingRepository;
import com.riseup.flimbit.repository.UserPromoCodeRepository;
import com.riseup.flimbit.repository.UserRepository;
import com.riseup.flimbit.repository.UserStatusRespository;
import com.riseup.flimbit.request.EmailRequest;
import com.riseup.flimbit.request.LoginRequest;
import com.riseup.flimbit.request.PanRequest;
import com.riseup.flimbit.request.PhoneRegValidateRequest;
import com.riseup.flimbit.request.PhoneRegisterRequest;
import com.riseup.flimbit.request.RefreshTokenRequest;
import com.riseup.flimbit.request.RegisterUserName;
import com.riseup.flimbit.request.StatusRequest;
import com.riseup.flimbit.request.UserRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.PhoneOtpResponse;
import com.riseup.flimbit.response.SuccessResponse;
import com.riseup.flimbit.response.TokenResponse;
import com.riseup.flimbit.response.UserProfileResponse;
import com.riseup.flimbit.security.UserContext;
import com.riseup.flimbit.security.UserContextHolder;
import com.riseup.flimbit.service.UserRegisterService;
import com.riseup.flimbit.service.UserWalletBalanceService;
import com.riseup.flimbit.utility.CodeGenerator;
import com.riseup.flimbit.utility.CommonUtilty;
import com.riseup.flimbit.utility.HttpResponseUtility;
import com.riseup.flimbit.utility.JwtService;
import com.riseup.flimbit.utility.TokenEncryptor;

import jakarta.transaction.Transactional;

@Service
public class UserRegisterServiceImp implements UserRegisterService {

	Logger logger = LoggerFactory.getLogger(UserRegisterServiceImp.class);

	private Map<String, PhoneOtpResponse> phoneRegOtpCache = new HashMap<>();
	@Autowired
	UserRepository userRepository;

	@Autowired
	UserStatusRespository userStatusRespository;

	@Autowired
	JwtService jwtService;

	@Autowired
	PromoCodeRepository promoCodeRepository;

	@Autowired
	PromotionTypeRepository promotionTypeRepo;

	@Autowired
	UserPromoCodeRepository userPromoCodeRepository;

	@Autowired
	UserWalletBalanceService userWalletServcie;

	@Autowired
	ReferralTrackingRepository referralTrackingRepository;

	@Autowired
	AuditLogServiceImp audit;
	
	@Autowired
	TokenEncryptor tokenEncryptor;
	
	
	@Autowired
	OtpService  smsService;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	PanService  panService;
	
	 @Autowired
	    SurePassAuthService  surePassAuthService;
	
	 @Autowired
	 ConfigCacheService  configCacheService;
	 
	 @Autowired
	 LanguageRepository languageRepo;
	 
	@Override
	public CommonResponse generateRegPhoneOtp(PhoneRegisterRequest phoneRegRequest) {
		// TODO Auto-generated method stub
		
		Optional<User> existUser = userRepository.findByphoneNumber(phoneRegRequest.getPhoneNumber());
		if (existUser.isPresent()) {
			return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.USER_FOUND).build();
		}
		//String otpCode = String.valueOf(new Random().nextInt(900000) + 100000);
		//PhoneOtpResponse phoneOtpResponse = PhoneOtpResponse.builder().code(otpCode).expiryTime(Instant.now()).build();
		//phoneRegOtpCache.put(phoneRegRequest.getPhoneNumber(), phoneOtpResponse);
		// trigger sms service to send otp
		OtpResponse response =	smsService.sendOtp(phoneRegRequest.getPhoneNumber(), "noneed auto generate");
		return CommonResponse.builder().status(Messages.STATUS_SUCCESS).result(response).build();

	}
	
	
	
	@Transactional
	@Override
	public CommonResponse validateRegPhoneOtp(PhoneRegValidateRequest phRegValiReq, String deviceId) {
		// TODO Auto-generated method stub

	/*	PhoneOtpResponse phoneOtpRes = phoneRegOtpCache.get(phRegValiReq.getPhoneNumber());
		if (phoneOtpRes == null)
			return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.REG_PHONE_NUMBER_NOT_FOUND)
					.build();

		boolean isExpired = Duration.between(phoneOtpRes.getExpiryTime(), Instant.now()).toMinutes() > 5;
		if (isExpired) {
			phoneRegOtpCache.remove(phRegValiReq.getPhoneNumber());
			return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.REG_OTP_EXPIRY).build();

		}
		
		
		try {
			emailService.sendVerificationEmail("rojer127@gmail.com", "test");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  */
		

		Optional<User> existUser = userRepository.findByphoneNumber(phRegValiReq.getPhoneNumber());
		if (existUser.isPresent()) {
			return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.USER_FOUND).build();
		}
		OtpResponse response =	smsService.verifyOtp(phRegValiReq.getSessionId(), phRegValiReq.getOtp());
		//boolean isValid = phoneOtpRes.getCode().equals(phRegValiReq.getOtp());
		
		boolean isValid = response.getStatus().equalsIgnoreCase("success") ? true : false;
		logger.info(phRegValiReq.getPhoneNumber() + ":" + isValid + ": " +isValid+ " "
				+ phRegValiReq.getOtp());

		if (isValid) {

			
			
			String tokenKey = phRegValiReq.getPhoneNumber() + ":" + deviceId;
	    	String token = jwtService.createMobileToken(tokenKey, false);
			String refreshToken = jwtService.createMobileToken(tokenKey, true);
			String tokenEnc = tokenEncryptor.encrypt(token);
			String refreshTokenEnc = tokenEncryptor.encrypt(refreshToken);

			
			User user = new User();
			user.setPhoneNumber(phRegValiReq.getPhoneNumber());
			user.setStatus(Messages.USER_ACTIVE);
			user.setAccessKey(tokenEnc);
			user.setRefreshToken(refreshTokenEnc);
			user.setDeviceId(deviceId);
			
			int id = userRepository.save(user).getId();
			logger.info("id " + id);
			UserStatus userStatus = new UserStatus();
			userStatus.setUserId(Integer.parseInt(id + ""));
			userStatus.setPhoneVerified(true);
			userStatusRespository.save(userStatus);
			TokenResponse tokenRes = TokenResponse.builder().accessToken(tokenEnc).refreshToken(refreshTokenEnc)
					.accessTokenExpiry(jwtService.extractExpiration(token).toInstant())
					.refreshTokenExpiry(jwtService.extractExpiration(refreshToken).toInstant()).build();
			return CommonResponse.builder().status(Messages.STATUS_SUCCESS).result(tokenRes).build();

		}

		return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.REG_OTP_NOT_MATCH).build();

	}

	@Override
	@Transactional
	public String updateUser(RegisterUserName userRequest, String phoneNumber) {
		// TODO Auto-generated method stub
		Optional<User> userOpt = userRepository.findByphoneNumber(phoneNumber);
		if (userOpt.isPresent()) {
			User user = userOpt.get();
			UserStatus userStatus = userStatusRespository.findByuserId(Integer.parseInt(user.getId() + ""))
					.orElseGet(() -> {
						UserStatus userStatusNew = new UserStatus();
						userStatusNew.setUserId(Integer.parseInt(user.getId() + ""));
						return userStatusNew;
					});
			if (StringUtils.isBlank(userRequest.getFirstName()) || StringUtils.isBlank(userRequest.getLastName())) {
			    throw new RuntimeException("First name/Last name cannot be empty");
			}
				

			
				user.setFirstName(userRequest.getFirstName());
				user.setLastName(userRequest.getLastName());				
					userStatus.setNamesVerified(true);
			
		

			userRepository.save(user);
			userStatusRespository.save(userStatus);
			/*long userIdLong = user.getId();
			List<String> referalList = List.of(PromotionTypeEnum.WELCOME.name().toLowerCase());
			List<UserPromoCode> listUserPromo = userPromoCodeRepository.findByUserIdAndTypeCodesIgnoreCase(userIdLong,
					referalList);
			boolean isWelcomeUser = listUserPromo == null || listUserPromo.size() == 0 ? true : false;
			logger.info("size x" + listUserPromo.size() + " " + isWelcomeUser);
			if (isWelcomeUser) {
				logger.info("new user welcome promocode applied");
				//assignWelecomePromoForNewUser(user);
				//generateReferralPromoCodeForUser(user);
			} */

		//	if (CommonUtilty.checkEmptyOrNull(userRequest.getPromoCode()) && isWelcomeUser) {

				//System.out.println("promocode is available " + userRequest.getPromoCode());

				//applyReferralCodeDuringSignup(user, userRequest.getPromoCode());
		//	}

			return "User name has been added successfully.";
		}
		
		throw new RuntimeException(Messages.REG_PHONE_NUMBER_NOT_FOUND);

	

	}

	public void assignWelecomePromoForNewUser(User user) {

		System.out.println(PromotionTypeEnum.WELCOME.name() + ":" + StatusEnum.ACTIVE.getDescription());

		PromotionType promoType = promotionTypeRepo
				.findByTypeCodeIgnoreCaseAndStatusIgnoreCase(PromotionTypeEnum.WELCOME.name().toLowerCase(),
						StatusEnum.ACTIVE.name().toLowerCase())
				.orElseThrow(() -> new RuntimeException("Promo Type not found"));
		;

		if (Optional.ofNullable(promoType).isPresent()) {

			PromoCode promo = PromoCode.builder().promoCode("Welcome Promo " + user.getId())
					// .promoType(promoType.getTypeCode()).usesLeft(promoType.getUserCount())
					// .expiryDate(Date.valueOf(LocalDate.now().plusDays(promoType.getExpiryDays())))
					.status(StatusEnum.INACTIVE.getDescription().toLowerCase())
					// .promoTypeId(Integer.parseInt(promoType.getId() + ""))
					.createdAt(new Timestamp(System.currentTimeMillis()))
					.updatedAt(new Timestamp(System.currentTimeMillis())).build();

			promo = promoCodeRepository.save(promo);
			UserPromoCode userPromo = UserPromoCode.builder().userId(user.getId()).promoId(promo.getId())
					.usedAt(new Timestamp(System.currentTimeMillis())).build();

			userPromoCodeRepository.save(userPromo);

			if (promoType.getPrizeAmount() > 0) {
				userWalletServcie.addShareCash(user.getId(), BigDecimal.valueOf(promoType.getPrizeAmount()));
			}

		}

		System.out.println(" welcome end ");

	}

	private void generateReferralPromoCodeForUser(User user) {
		String code = CodeGenerator.generateCode(user.getFirstName());
		System.out.println(" generateReferralPromoCodeForUser ");

		// Ensure uniqueness
		while (promoCodeRepository.findByPromoCode(code) == null) {
			code = CodeGenerator.generateCode(user.getFirstName());
		}

		// Save new referral promo code
		PromotionType promoType = promotionTypeRepo
				.findByTypeCodeIgnoreCaseAndStatusIgnoreCase(PromotionTypeEnum.REFERRAL.name().toLowerCase(),
						StatusEnum.ACTIVE.name().toLowerCase())
				.orElseThrow(() -> new RuntimeException("Promo Type not found in genereate refferal user"
						+ PromotionTypeEnum.REFERRAL.name().toLowerCase()));

		if (promoType != null) {

			PromoCode promo = PromoCode.builder().promoCode(code)
					.status(StatusEnum.ACTIVE.getDescription().toLowerCase())
					.createdAt(new Timestamp(System.currentTimeMillis()))
					.updatedAt(new Timestamp(System.currentTimeMillis())).build();

			promo = promoCodeRepository.save(promo);

			UserPromoCode userPromo = UserPromoCode.builder().userId(user.getId()).promoId(promo.getId())
					.usedAt(new Timestamp(System.currentTimeMillis())).build();

			userPromoCodeRepository.save(userPromo);

		}

		System.out.println(" generateReferralPromoCodeForUser end");

		// Link to user

	}

	private void applyReferralCodeDuringSignup(User newUser, String promoCodeUsed) {
		PromoCode promo = promoCodeRepository.findByPromoCode(promoCodeUsed)
				.orElseThrow(() -> new RuntimeException("Promo Code is not found"));
		System.out.println(" applyReferralCodeDuringSignup ");

		if (promo != null) {
			if (!promo.getStatus().equalsIgnoreCase("Active")) {

				if (promo.getStatus().equalsIgnoreCase(StatusEnum.ACTIVE.name())) {

					promo.setStatus(StatusEnum.INACTIVE.name().toLowerCase());
					promo.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
					promoCodeRepository.save(promo);

				}

				throw new RuntimeException("Promo code expired or used up");
			} else {
				PromotionType promoType = promotionTypeRepo
						.findByTypeCodeIgnoreCaseAndStatusIgnoreCase(PromotionTypeEnum.REFERRAL.name().toLowerCase(),
								StatusEnum.ACTIVE.name().toLowerCase())
						.orElseThrow(() -> new RuntimeException("Promo Type not found in genereate refferal user"
								+ PromotionTypeEnum.REFERRAL.name().toLowerCase()));

				userWalletServcie.addShareCash(newUser.getId(), BigDecimal.valueOf(promoType.getPrizeAmount()));

				// Track referral
				referralTrackingRepository.save(ReferralTracking.builder()
						.referrerUserId(getOwnerOfPromo(promo.getId())).referredUserId(newUser.getId())
						.promoCode(promoCodeUsed).creditAmount(BigDecimal.valueOf(10)).creditedTo("Referred")
						.shareCashApplied(true).build());

				// Save usage in user_promo_codes
				userPromoCodeRepository.save(UserPromoCode.builder().userId(newUser.getId()).promoId(promo.getId())
						.usedAt(new Timestamp(System.currentTimeMillis())).build());

				// Decrease uses left
				promoCodeRepository.save(promo);

			}

		}

		// Give ₹10 share cash to new user
	}

	public int getOwnerOfPromo(int promoId) {
		return userPromoCodeRepository.findFirstByPromoId(promoId).stream().map(UserPromoCode::getUserId).findFirst()
				.orElse(null); // or throw exception if mandatory
	}

	@Override
	public CommonResponse genRefreshToken(String deviceId, String phoneNumber, RefreshTokenRequest refreshRequest) {
		// TODO Auto-generated method stub
		
		logger.info("entering into getRefresh token");
		UserContext  loginUser = UserContextHolder.getContext();
		
		phoneNumber = loginUser.getPhone();
		
		deviceId  = loginUser.getDeviceId();
		logger.info("entering into getRefresh token "+ phoneNumber  + " " + deviceId);
		
        String decriptedToken = tokenEncryptor.decrypt(refreshRequest.getRefreshToken());

		
		boolean isValid = jwtService.validateMobileToken(decriptedToken, deviceId, phoneNumber);

		if (!isValid)
		{
		   throw new RuntimeException("Token invalid");
		}
		
		Optional<User> userOpt = userRepository.findByphoneNumber(phoneNumber);
		if (userOpt.isPresent()) {
			String token = jwtService.createMobileToken(phoneNumber + ":" + deviceId, false);
			String refreshToken = jwtService.createMobileToken(phoneNumber + ":" + deviceId, true);

			String tokenEnc = tokenEncryptor.encrypt(token);
			String refreshTokenEnc = tokenEncryptor.encrypt(refreshToken);
			
			User user = userOpt.get();
			    user.setAccessKey(tokenEnc);
		        user.setRefreshToken(refreshTokenEnc);
		        user.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
		        userRepository.save(user);		
				TokenResponse tokenRes = TokenResponse.builder().accessToken(tokenEnc).refreshToken(refreshTokenEnc)
						.accessTokenExpiry(jwtService.extractExpiration(token).toInstant())
						.refreshTokenExpiry(jwtService.extractExpiration(refreshToken).toInstant()).build();

			return CommonResponse.builder().status(Messages.STATUS_SUCCESS).result(tokenRes).build();

		} else
			return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.REG_PHONE_NUMBER_NOT_FOUND)
					.build();

	}

	@Override
	public Page<UserWithStatusWebDto> fetchAllUsersWithStatus(int language, int movie, String status, String searchText,
			int start, int length, String sortColumn, String sortOrder) {
		// TODO Auto-generated method stub

		int page = start / length;
		Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
		Pageable pageable = PageRequest.of(page, length, sort);

		return userRepository.fetchAllUsersWithStatus(language, movie, status, searchText, pageable);
	}

	@Transactional
	@Override
	public User updateUserStatus(StatusRequest statusReq) {
		// TODO Auto-generated method stub
		User user = userRepository.findById(statusReq.getId())
				.orElseThrow(() -> new RuntimeException("User is not found " + statusReq.getId()));
		if (user.getStatus().equalsIgnoreCase(statusReq.getStatus()))
			throw new RuntimeException("Same status can be changed");

		audit.logAction(UserContextHolder.getContext().getUserId(),ActionType.UPDATE.name()
				, EntityName.USER.name(), user.getId(), " user status changed from " + user.getStatus()  +" to " + statusReq.getStatus(), statusReq);

		
		user.setStatus(statusReq.getStatus().toLowerCase());
		user.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
		
		
	
		return userRepository.save(user);
	}

	@Override
	public CommonResponse validateOtp(String authHeader,String deviceId) {
		// TODO Auto-generated method stub
		CommonResponse response = null ;
        try {
            // Extract token from "Bearer <token>"
        	 String token = authHeader.replace("Bearer ", "").trim();
             if(token == null || token.trim().length()==0)
                  return null;        	 
        	 
            // Extract username/email/device from token (depending on how you built it)
            String usernameOrEmail = jwtService.extractUsername(token);

            // Call your custom validation method
            response = jwtService.validateToken(token, deviceId, usernameOrEmail);
            
	
            	

        } catch (Exception ex) {
            response.setStatus("FAILURE");
            response.setMessage("Invalid or malformed token");
              }

		return response;
	}

	@Override
	public TokenResponse createMobileToken(String deviceId,String phoneNumber,LoginRequest loginRequest) 	
	{
		// TODO Auto-generated method stub
		logger.info("***** create token starts  for " + phoneNumber);
		User user = userRepository.findByphoneNumber(phoneNumber)
		.orElseThrow(() -> new RuntimeException("user is not found  for " + phoneNumber));
		
		if(!user.getPassword().trim().equalsIgnoreCase(loginRequest.getPassword().trim())  &&
				!user.getDeviceId().trim().equalsIgnoreCase(loginRequest.getDeviceId().trim())
				)
		{
			
			logger.info("Password and device Id is not matching in db");
			throw new RuntimeException("Password and device Id is not matching");
		}
		
		String tokenKey = user.getPhoneNumber() + ":" + user.getDeviceId();
    	String token = jwtService.createMobileToken(tokenKey, false);
		String refreshToken = jwtService.createMobileToken(tokenKey, true);
		String tokenEnc = tokenEncryptor.encrypt(token);
		String refreshTokenEnc = tokenEncryptor.encrypt(refreshToken);
        user.setAccessKey(tokenEnc);
        user.setRefreshToken(refreshTokenEnc);
        user.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);		
		TokenResponse tokenRes = TokenResponse.builder().accessToken(tokenEnc).refreshToken(refreshTokenEnc)
				.accessTokenExpiry(jwtService.extractExpiration(token).toInstant())
				.refreshTokenExpiry(jwtService.extractExpiration(refreshToken).toInstant()).build();

		
		logger.info("***** create token ends  for " + phoneNumber);
		return tokenRes;
	}

	@Override
	public User validateMobileUserToken(String phoneNumber, String deviceId,String token) {
		// TODO Auto-generated method stub
		logger.info("***** Validate validateMobileUserToken starts ******* " + phoneNumber);
		try   
		{
			
			if(token == null || token.trim().length()==0)
                return null;  
           String decriptedToken = tokenEncryptor.decrypt(token);
           logger.info("user comming");
			
           Optional<User> userOpt = userRepository.findByphoneNumber(phoneNumber);
           if(!userOpt.isPresent())
           {
    		    logger.info("user is not found for Phone Number" + phoneNumber);

           }
           User user = userOpt.get();
         String userToken = user.getAccessKey().trim();
      	 String refreshToken = user.getRefreshToken().trim();
      	 String providedToken = token.trim();
    	 
     	// Debug: Print lengths and contents
     	 
     	


      	 if (!userToken.trim().equals(providedToken) && !refreshToken.trim().equals(providedToken)) {
 		    logger.info("Token does not match with refresh or access token. Device ID: " + phoneNumber);
 		    return null;
 		  }      
      	 if(!user.getDeviceId().trim().equalsIgnoreCase(deviceId.trim()))
      	 {
      		 logger.info("Device Id is not matched . Device ID: " + deviceId + " : db :"+user.getDeviceId());
  		    return null;
      	 }
      	 
      	 
      boolean isValid =	jwtService.validateMobileToken(decriptedToken, user.getDeviceId(), user.getPhoneNumber());
           
        if(!isValid)
        {
        	logger.info("Token is not valid " + phoneNumber);
        	return null;
        }
			return user;
		}
		catch(Exception e)
		{
			logger.info(e.getMessage());
			
		}
		
		return null;
	}


    @Transactional
	@Override
	public SuccessResponse sendVerificationEmail(EmailRequest emailRequest) {
    	
    //	System.out.println(configCacheService.getConstant("max_invest_amount"));
		
	 UserContext loginUser = 	UserContextHolder.getContext();
	 
	    String otp =  String.valueOf((int)(Math.random() * 900000) + 100000);

		// TODO Auto-generated method stu
	 logger.info("user " + loginUser.getDeviceId() + ":" + loginUser.getPhone() + ": otp :" +otp);
	 
	 if (StringUtils.isBlank(emailRequest.getEmail()) ) {
		    throw new RuntimeException("Email name could not be empty");
		}
	 
	 User user = userRepository.findByphoneNumber(loginUser.getPhone()).orElse(null);
	 
	 
				
	 if(user == null  )
		 throw new RuntimeException("User is not found " + loginUser.getPhone());
	 
	 
	 
	 
	  boolean isEmailAleradyActivate = userRepository.existsByEmail(emailRequest.getEmail());
	  
	  if(isEmailAleradyActivate)
	  {
		   User emailUser = userRepository.findByEmail(emailRequest.getEmail()).orElse(null);
		   
		   if(emailUser != null && emailUser.getPhoneNumber()  != user.getPhoneNumber())
				    throw new RuntimeException("email is already registerd with another account  " );

	  }

	  
	  
             	  
     user.setEmail(emailRequest.getEmail());
	 
	 user.setVerificationCode(otp);
	 user.setUpdatedDate(new Timestamp(System.currentTimeMillis()));	
	 user.setVerificationCodeExpiry(Timestamp.from(Instant.now().plusSeconds(10 * 60)));
	 
	 try {
		emailService.sendVerificationEmail(emailRequest.getEmail() , otp);
		userRepository.save(user);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return  SuccessResponse.builder().status(Messages.STATUS_FAILURE).message("mail send eror").build();

	}
	 
		return  SuccessResponse.builder().status(Messages.SUCCESS).message("mail send").build();
	}



	@Override
	public SuccessResponse verifyEmail(String code) {
		// TODO Auto-generated method stub
		
		 UserContext loginUser = 	UserContextHolder.getContext();
		 
		    String otp =  String.valueOf((int)(Math.random() * 900000) + 100000);

			// TODO Auto-generated method stu
		 logger.info("verifyEmail  " + loginUser.getDeviceId() + ":" + loginUser.getPhone() + ": code :" +code);
		 
		 User user = userRepository.findByphoneNumber(loginUser.getPhone()).orElse(null);
					
		 if(user == null  )
			 throw new RuntimeException("User is not found " );
		 
		 
		 if (user.getVerificationCode() == null || 
			        !user.getVerificationCode().equals(code)) {
			 throw new RuntimeException("Verification code is not equal" );
			    }
		 
		 if (user.getVerificationCodeExpiry() != null &&
				    user.getVerificationCodeExpiry().before(new Timestamp(System.currentTimeMillis()))) {
			 throw new RuntimeException("Verification code has expired. Please request a new one. " );

				}	
		 
		 UserStatus userStatus = userStatusRespository.findByuserId(user.getId())
			        .orElseGet(() -> new UserStatus());
		 
		 if(userStatus.getUserId() != user.getId())
			 userStatus.setUserId(user.getId());
		 userStatus.setEmailVerified(true);
				                 
          userStatusRespository.save(userStatus);	 
		 
		return SuccessResponse.builder().status(Messages.STATUS_SUCCESS).message("Email verified successfully").build();
	}


   @Transactional
	@Override
	public SuccessResponse initiatePan(PanRequest panRequest) {
		// TODO Auto-generated method stub
		 UserContext loginUser = 	UserContextHolder.getContext();
		
		 User user = userRepository.findByphoneNumber(loginUser.getPhone()).orElse(null);
			
		 if(user == null  )
			 throw new RuntimeException("User is not found " );
		 
          boolean isPanActive = userRepository.existsByPan(panRequest.getPanNumber());
		 
		 if (isPanActive) {
			 
			 User existUserPan = userRepository.findByPanId(panRequest.getPanNumber()).orElse(null);
			 
			 if(existUserPan != null)
			 {
				 if(existUserPan.getPhoneNumber().equals(user.getPhoneNumber()))
					    throw new RuntimeException("PAN is already verified for this number in verified status");

				 else
					    throw new RuntimeException("PAN is already available in verified status with other phone number");
 
				 
			 } 
			 else 			 
			    throw new RuntimeException("PAN is already available in verified status with other phone number");
			}		 
		 
     PanVerificationResponse  panVerResponse =	 panService.startVerification(panRequest);
     
     if(panVerResponse == null)
		    throw new RuntimeException("PAN Third Party Api is failed  ");
     
     if( panVerResponse.isFinalStatus() )
     {
    	    logger.info(" Going to update user pan update status");
    	    user.setPanId(panRequest.getPanNumber());
    	    UserStatus userStatus = userStatusRespository.findByuserId(user.getId())
    	            .orElseThrow(() -> new RuntimeException("User status is not found"));
    	    userStatus.setUserId(user.getId());
    	    userStatus.setPanVerified(true);
    		user.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
    	    userStatusRespository.save(userStatus);    
    	    userRepository.save(user);
    		return SuccessResponse.builder().status(Messages.STATUS_SUCCESS).message("PAN verified successfully").build();

    	    
     }
     else
     {
    	    logger.info(" Going to update user pan update status");
    	    if(!panVerResponse.isSetDobMatch() )
    		    throw new RuntimeException("PAN DOB is not matched  ");
    	    if(!panVerResponse.isNameAsPerPanMatch() )
    		    throw new RuntimeException("PAN Name is not matched  ");
    	    	
     }
     
	
	logger.info ("Pan Response " + panVerResponse);
    throw new RuntimeException("Some issue in PAN verification ");

	}



@Override
public SuccessResponse verifyLanguage(long langId) {
	// TODO Auto-generated method stub
	
  Language  language =languageRepo.findById(langId).orElseThrow(() -> new RuntimeException("Given language is not found "));
  UserContext loginUser = UserContextHolder.getContext();
	 User user = userRepository.findByphoneNumber(loginUser.getPhone()).orElse(null);
		
	 if(user == null  )
		 throw new RuntimeException("User is not found " );
	 int languageId = (int) langId;
	 UserStatus userStatus = userStatusRespository.findByuserId(user.getId())
			         .orElseThrow(() -> new RuntimeException("User status is not found for user"));
	           
	 
	 user.setLanguage(languageId);
	 user.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
	 
	 userStatus.setLanguageVerified(true);
	  userStatusRespository.save(userStatus);    
	   userRepository.save(user);
	 

		return SuccessResponse.builder().status(Messages.STATUS_SUCCESS).message("Language 	verified successfully").build();
}



@Override
public UserProfileResponse getUserProfileMobile() {
	// TODO Auto-generated method stub
	UserContext loginUser = UserContextHolder.getContext();
	 User user = userRepository.findByphoneNumber(loginUser.getPhone()).orElse(null);
		
	 if(user == null  )
		 throw new RuntimeException("User is not found " );
	 
	 UserStatus userStatus = userStatusRespository.findByuserId(user.getId())
	         .orElseThrow(() -> new RuntimeException("User status is not found for user"));
	 user.setAccessKey("");
	 user.setRefreshToken("");
	 
       
	 return UserProfileResponse.builder().user(user).userStatus(userStatus).build()	;
}

}
