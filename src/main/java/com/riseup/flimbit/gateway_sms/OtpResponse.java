package com.riseup.flimbit.gateway_sms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpResponse {
    private String status;       // "success" or "failed"
    private String sessionId;    // session or SID from provider
    private String message;      // human-readable message
}