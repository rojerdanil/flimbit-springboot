package com.riseup.flimbit.response;

import com.riseup.flimbit.entity.User;
import com.riseup.flimbit.entity.UserStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserProfileResponse {
	
	User  user;
	UserStatus userStatus;

}
