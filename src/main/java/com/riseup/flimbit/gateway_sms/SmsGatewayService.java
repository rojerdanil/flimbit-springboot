package com.riseup.flimbit.gateway_sms;

import org.springframework.http.ResponseEntity;

public interface SmsGatewayService {
	OtpResponse sendOtp(String phoneNumber, String otp);
	OtpResponse verifyOtp(String sessionId, String otp);
}
