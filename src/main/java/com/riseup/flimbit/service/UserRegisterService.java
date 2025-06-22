package com.riseup.flimbit.service;

import com.riseup.flimbit.request.PhoneRegValidateRequest;
import com.riseup.flimbit.request.PhoneRegisterRequest;
import com.riseup.flimbit.request.RefreshTokenRequest;
import com.riseup.flimbit.request.UserRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.PhoneOtpResponse;

public interface UserRegisterService {
	public CommonResponse  generateRegPhoneOtp(PhoneRegisterRequest phoneRegRequest);
	public CommonResponse  validateRegPhoneOtp(PhoneRegValidateRequest phRegValiReq,String device_id);
	public CommonResponse updateUser(UserRequest userRequest,String phoneNumber);
	public CommonResponse genRefreshToken(String deviceId,String phoneNumber,RefreshTokenRequest refreshRequest);

}
