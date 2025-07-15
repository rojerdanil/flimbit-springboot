package com.riseup.flimbit.request;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromoRewardMapRequest {

	
	 	private String promoCode;

	    private String promoType;

	    private Integer usesLeft;

	    private Integer expiryDays;

	    private String status ;
	    
	    private int promoTypeId;
	    
	    private String promoCodeUsage;
	    
	    private int rewardId;
	    
	    private String activationDate;

}
