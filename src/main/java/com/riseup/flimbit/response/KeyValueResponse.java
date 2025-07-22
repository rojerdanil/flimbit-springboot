package com.riseup.flimbit.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KeyValueResponse {
	
	String key;
	String value;
	

}
