package com.riseup.flimbit.entity.dto;

import java.sql.Timestamp;

public interface PartnerDTO {
	
	     int getId();
	     String getName();
	     String getType();
	     String getContactPerson();
	     String getPhone();
	     String getEmail();
	     String getAddress();
	     String getLogoUrl();
	     String getDescription();
	     String getStatus();
	     int getLanguageId();
	     Timestamp getLastLoginDate();
	     Timestamp getCreatedAt();
	     Timestamp getUpdatedAt();
         String getLanguageName();
         




}
