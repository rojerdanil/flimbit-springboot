package com.riseup.flimbit.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SuccessResponse {
	
	String status;
	
	String message;

}
