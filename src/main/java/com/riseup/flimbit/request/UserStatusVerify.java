package com.riseup.flimbit.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class UserStatusVerify {
	boolean isPhoneVerified;
	boolean isEmailVerified;
	boolean isPanVerified;
	boolean isNamesVerified;
	boolean isLanguageVerified;

}
