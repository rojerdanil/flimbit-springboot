package com.riseup.flimbit.gateway_sms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwoFactorResponse {
	 @JsonProperty("Status")
	    private String status;

	    @JsonProperty("Details")
	    private String details;

	    @JsonProperty("OTP")
	    private String otp;
}