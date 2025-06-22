package com.riseup.flimbit.response;

import java.time.Instant;
import java.time.LocalDateTime;

import org.apache.logging.log4j.CloseableThreadContext.Instance;

import com.riseup.flimbit.request.PhoneRegValidateRequest;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class PhoneOtpResponse {
	private String code;
    private Instant expiryTime;

	

}
