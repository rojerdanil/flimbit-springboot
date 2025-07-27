package com.riseup.flimbit.entity.dto;

public interface AdminUserDTO {
	
	     int getId();

	    String getName();

	    String getEmail();
	    String getPasswordHash();

	    int getRoleId();

	    String getStatus();

	    String getCreatedAt();

	    String getUpdatedAt();
	    
	    String getRoleName();
	    
	    String getLastLogin();
	    boolean getIsVerified();
	    
	    String getPhone();      
	   
	    boolean getIsEmailVerified();
	    boolean getIsPhoneVerified();
	    
	    // maps to phone

}
