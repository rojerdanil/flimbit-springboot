package com.riseup.flimbit.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class PhoneRegisterRequest {
	String phoneNumber;

}
