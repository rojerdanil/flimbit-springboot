package com.riseup.flimbit.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AdminUserRequest {

	  private String name;
	    private String email;
	    private String password;     // Plain password from request
	    private int roleId;
	    private String status;
	    private boolean isVerified;
	    private String phone;
}
