package com.riseup.flimbit.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class UserRequest {
	String panId;
	String firstName;
	String lastName;
	String email;
	String status;
	String language;
	String promoCode;
	UserStatusVerify userStatusVerify;



}
