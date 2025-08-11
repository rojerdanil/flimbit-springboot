package com.riseup.flimbit.gateway.pan;

import com.riseup.flimbit.request.PanRequest;

public interface PanVerificationService {
    
    /**
     * Initiates PAN verification and sends OTP to Aadhaar-linked mobile.
     * @param panNumber - User's PAN number
     * @return response with status and transactionId
     */
    PanVerificationResponse initiateVerification(PanRequest panRequest);

    /**
     * Verifies OTP for a given transaction ID.
     * @param transactionId - ID received from initiate step
     * @param otp - OTP entered by the user
     * @return response with verification result
     */
    PanVerificationResponse verifyOtp(String transactionId, String otp);
}
