package com.riseup.flimbit.gateway_sms;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service

public class TwilioSmsGatewayService implements SmsGatewayService {

    @Override
    public OtpResponse sendOtp(String phoneNumber, String otp) {
        System.out.println("Sending OTP via Twilio...");
        // Call Twilio API
        return null;
    }

    @Override
    public OtpResponse verifyOtp(String sessionId, String otp) {
        return null;
    }
}
