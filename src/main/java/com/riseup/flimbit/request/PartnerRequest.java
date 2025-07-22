package com.riseup.flimbit.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerRequest {
	
	 private String name;
	    private String type;
	    private String contactPerson;
	    private String phone;
	    private String email;
	    private String address;
	    private String logoUrl;
	    private String description;
	    private String status;
	    private int languageId;


}
