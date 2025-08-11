package com.riseup.flimbit.gateway.pan;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PanVerificationResponse {
    private String status;       // OTP_SENT, VERIFIED, FAILED
    private String transactionId;
    private String fullName;
    private String message;
    private boolean NameAsPerPanMatch;
    private boolean setDobMatch;
    private boolean finalStatus;

    
    
    // getters and setters
}
