package com.riseup.flimbit.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class CommonResponse {
	
   String status;
   String message;
   Object result;
   

}
