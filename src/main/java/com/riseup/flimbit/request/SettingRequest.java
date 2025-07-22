package com.riseup.flimbit.request;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SettingRequest {
	
	 private String key;

	    private String value;

	    private String description;

	    private String type;

	    private String groupName;


}
