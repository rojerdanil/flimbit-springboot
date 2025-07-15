package com.riseup.flimbit.response.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyValueDtoResponse {
	
	   private String key;
	    private String value;
	    public KeyValueDtoResponse(String key, String value) {
	        this.key = key;
	        this.value = value;
	    }

}
