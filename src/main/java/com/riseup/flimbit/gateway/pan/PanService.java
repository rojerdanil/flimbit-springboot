package com.riseup.flimbit.gateway.pan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.request.PanRequest;

@Service
public class PanService {

    @Autowired
    private PanVerificationFactory panVerificationFactory;

    public PanVerificationResponse startVerification(PanRequest panRequest) {
        // ✅ Dynamically get active provider based on DB setting or default
        PanVerificationService panService = panVerificationFactory.getPanService();

        // ✅ Call initiate verification
        return panService.initiateVerification(panRequest);
    }

    public PanVerificationResponse verifyOtp(String transactionId, String otp) {
        PanVerificationService panService = panVerificationFactory.getPanService();
        return panService.verifyOtp(transactionId, otp);
    }
}
