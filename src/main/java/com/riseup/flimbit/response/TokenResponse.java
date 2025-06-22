package com.riseup.flimbit.response;

import java.time.Instant;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenResponse {
	
	String accessToken;
	String refreshToken;
	Instant accessTokenExpiry;
	Instant refreshTokenExpiry;

}
