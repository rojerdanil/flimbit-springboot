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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.ActionType;
import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.constant.PromotionTypeEnum;
import com.riseup.flimbit.constant.StatusEnum;
import com.riseup.flimbit.entity.PromoCode;
import com.riseup.flimbit.entity.PromotionType;
import com.riseup.flimbit.entity.ReferralTracking;
import com.riseup.flimbit.entity.User;
import com.riseup.flimbit.entity.UserPromoCode;
import com.riseup.flimbit.entity.UserStatus;
import com.riseup.flimbit.entity.dto.UserWithStatusDTO;
import com.riseup.flimbit.repository.PromoCodeRepository;
import com.riseup.flimbit.repository.PromotionTypeRepository;
import com.riseup.flimbit.repository.ReferralTrackingRepository;
import com.riseup.flimbit.repository.UserPromoCodeRepository;
import com.riseup.flimbit.repository.UserRepository;
import com.riseup.flimbit.repository.UserStatusRespository;
import com.riseup.flimbit.request.PhoneRegValidateRequest;
import com.riseup.flimbit.request.PhoneRegisterRequest;
import com.riseup.flimbit.request.RefreshTokenRequest;
import com.riseup.flimbit.request.StatusRequest;
import com.riseup.flimbit.request.UserRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.PhoneOtpResponse;
import com.riseup.flimbit.response.TokenResponse;
import com.riseup.flimbit.security.UserContextHolder;
import com.riseup.flimbit.service.UserRegisterService;
import com.riseup.flimbit.service.UserWalletBalanceService;
import com.riseup.flimbit.utility.CodeGenerator;
import com.riseup.flimbit.utility.CommonUtilty;
import com.riseup.flimbit.utility.JwtService;

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
	
	@Override
	public CommonResponse generateRegPhoneOtp(PhoneRegisterRequest phoneRegRequest) {
		// TODO Auto-generated method stub
		Optional<User> existUser = userRepository.findByphoneNumber(phoneRegRequest.getPhoneNumber());
		if (existUser.isPresent()) {
			return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.USER_FOUND).build();
		}
		String otpCode = String.valueOf(new Random().nextInt(900000) + 100000);
		PhoneOtpResponse phoneOtpResponse = PhoneOtpResponse.builder().code(otpCode).expiryTime(Instant.now()).build();
		phoneRegOtpCache.put(phoneRegRequest.getPhoneNumber(), phoneOtpResponse);
		// trigger sms service to send otp
		return CommonResponse.builder().status(Messages.STATUS_SUCCESS).result(phoneOtpResponse).build();

	}

	@Override
	public CommonResponse validateRegPhoneOtp(PhoneRegValidateRequest phRegValiReq, String device_id) {
		// TODO Auto-generated method stub

		PhoneOtpResponse phoneOtpRes = phoneRegOtpCache.get(phRegValiReq.getPhoneNumber());
		if (phoneOtpRes == null)
			return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.REG_PHONE_NUMBER_NOT_FOUND)
					.build();

		boolean isExpired = Duration.between(phoneOtpRes.getExpiryTime(), Instant.now()).toMinutes() > 5;
		if (isExpired) {
			phoneRegOtpCache.remove(phRegValiReq.getPhoneNumber());
			return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.REG_OTP_EXPIRY).build();

		}

		boolean isValid = phoneOtpRes.getCode().equals(phRegValiReq.getOtp());
		logger.info(phRegValiReq.getPhoneNumber() + ":" + isValid + ": " + phoneOtpRes.getCode() + " "
				+ phRegValiReq.getOtp());

		if (isValid) {

			phoneRegOtpCache.remove(phRegValiReq.getPhoneNumber());
			String token = jwtService.generateToken(phRegValiReq.getPhoneNumber() + ":" + device_id, false);
			String refreshToken = jwtService.generateToken(phRegValiReq.getPhoneNumber() + ":" + device_id, true);
			User user = new User();
			user.setPhoneNumber(phRegValiReq.getPhoneNumber());
			user.setStatus(Messages.USER_ACTIVE);
			user.setAccessKey(token);
			int id = userRepository.save(user).getId();
			logger.info("id " + id);
			UserStatus userStatus = new UserStatus();
			userStatus.setUserId(Integer.parseInt(id + ""));
			userStatus.setPhoneVerified(true);
			userStatusRespository.save(userStatus);
			TokenResponse tokenRes = TokenResponse.builder().accessToken(token).refreshToken(refreshToken)
					.accessTokenExpiry(jwtService.extractExpiration(token).toInstant())
					.refreshTokenExpiry(jwtService.extractExpiration(refreshToken).toInstant()).build();
			return CommonResponse.builder().status(Messages.STATUS_SUCCESS).result(tokenRes).build();

		}

		return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.REG_OTP_NOT_MATCH).build();

	}

	@Override
	@Transactional
	public CommonResponse updateUser(UserRequest userRequest, String phoneNumber) {
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

			if (CommonUtilty.checkEmptyOrNull(userRequest.getFirstName())) {
				user.setFirstName(userRequest.getFirstName());
				if (userRequest.getUserStatusVerify().isNamesVerified())
					userStatus.setNamesVerified(true);
			}
			if (CommonUtilty.checkEmptyOrNull(userRequest.getLastName())) {
				user.setLastName(userRequest.getLastName());
			}
			if (CommonUtilty.checkEmptyOrNull(userRequest.getEmail())) {
				user.setEmail(userRequest.getEmail());
				if (userRequest.getUserStatusVerify().isEmailVerified())
					userStatus.setEmailVerified(true);
			}
			if (CommonUtilty.checkEmptyOrNull(userRequest.getPanId())) {
				user.setPanId(userRequest.getPanId());
				if (userRequest.getUserStatusVerify().isPanVerified())
					userStatus.setPanVerified(true);
			}
			if (CommonUtilty.checkEmptyOrNull(userRequest.getStatus())) {
				user.setStatus(userRequest.getStatus());
			}
			if (user.getLanguage() != 0) {
				user.setLanguage(userRequest.getLanguage());
				if (userRequest.getUserStatusVerify().isLanguageVerified())
					userStatus.setLanguageVerified(true);
			}

			userRepository.save(user);
			userStatusRespository.save(userStatus);
			long userIdLong = user.getId();
			List<String> referalList = List.of(PromotionTypeEnum.WELCOME.name().toLowerCase());
			List<UserPromoCode> listUserPromo = userPromoCodeRepository.findByUserIdAndTypeCodesIgnoreCase(userIdLong,
					referalList);
			boolean isWelcomeUser = listUserPromo == null || listUserPromo.size() == 0 ? true : false;
			System.out.println("size x" + listUserPromo.size() + " " + isWelcomeUser);
			if (isWelcomeUser) {
				System.out.println("new user welcome");
				assignWelecomePromoForNewUser(user);
				generateReferralPromoCodeForUser(user);
			}

			if (CommonUtilty.checkEmptyOrNull(userRequest.getPromoCode()) && isWelcomeUser) {

				System.out.println("promocode is available " + userRequest.getPromoCode());

				applyReferralCodeDuringSignup(user, userRequest.getPromoCode());
			}

			return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_UPATE_SUCCESS)
					.build();
		}

		return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.REG_PHONE_NUMBER_NOT_FOUND)
				.build();

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
		String refreshToken = refreshRequest.getRefreshToken();
		CommonResponse commonToken = jwtService.validateToken(refreshRequest.getRefreshToken(), deviceId, phoneNumber);

		if (commonToken.getStatus() != Messages.SUCCESS) {
			return commonToken;
		}
		String token = jwtService.generateToken(phoneNumber + ":" + deviceId, false);
		Optional<User> userOpt = userRepository.findByphoneNumber(phoneNumber);
		if (userOpt.isPresent()) {
			User user = userOpt.get();
			user.setAccessKey(token);
			userRepository.save(user);
			TokenResponse tokenRes = TokenResponse.builder().accessToken(token).refreshToken(refreshToken)
					.accessTokenExpiry(jwtService.extractExpiration(token).toInstant())
					.refreshTokenExpiry(jwtService.extractExpiration(refreshToken).toInstant()).build();

			return CommonResponse.builder().status(Messages.STATUS_SUCCESS).result(tokenRes).build();

		} else
			return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.REG_PHONE_NUMBER_NOT_FOUND)
					.build();

	}

	@Override
	public Page<UserWithStatusDTO> fetchAllUsersWithStatus(int language, int movie, String status, String searchText,
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

}
