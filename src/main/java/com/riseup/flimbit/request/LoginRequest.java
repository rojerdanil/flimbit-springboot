package com.riseup.flimbit.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
	private String userName;  // email, phone, or username
    private String password;
    private String deviceId;

}
