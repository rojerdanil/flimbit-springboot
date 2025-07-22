package com.riseup.flimbit.entity.dto;

public interface InfluencerDTO {
	
	Integer getId();
    String getFirstName();
    String getLastName();
    String getEmail();
    String getPhoneNumber();
    String getSocialMediaHandle();
    String getCreatedAt();
    String getUpdatedAt();
    
    // Campaign and Promo details
    String getCampaignName();
    String getPromoCode();
     String getStatus();
     String getLanguageName();
    

}
