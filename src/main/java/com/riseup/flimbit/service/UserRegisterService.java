package com.riseup.flimbit.service;

import org.springframework.data.domain.Page;

import com.riseup.flimbit.entity.MovieInvestment;
import com.riseup.flimbit.entity.User;
import com.riseup.flimbit.entity.dto.UserInvestmentSectionDTO;
import com.riseup.flimbit.entity.dto.UserWithStatusDTO;
import com.riseup.flimbit.request.PhoneRegValidateRequest;
import com.riseup.flimbit.request.PhoneRegisterRequest;
import com.riseup.flimbit.request.RefreshTokenRequest;
import com.riseup.flimbit.request.StatusRequest;
import com.riseup.flimbit.request.UserRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.PhoneOtpResponse;

public interface UserRegisterService {
	public CommonResponse  generateRegPhoneOtp(PhoneRegisterRequest phoneRegRequest);
	public CommonResponse  validateRegPhoneOtp(PhoneRegValidateRequest phRegValiReq,String device_id);
	public CommonResponse updateUser(UserRequest userRequest,String phoneNumber);
	public CommonResponse genRefreshToken(String deviceId,String phoneNumber,RefreshTokenRequest refreshRequest);

	public Page<UserWithStatusDTO> fetchAllUsersWithStatus(int language,int movie,String status,String searchText, int start, int length,
			String sortColumn, String sortOrder);
	
	User updateUserStatus(StatusRequest statusReq);


}
