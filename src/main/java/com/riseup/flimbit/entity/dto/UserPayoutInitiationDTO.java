package com.riseup.flimbit.entity.dto;

import java.math.BigDecimal;

public interface UserPayoutInitiationDTO {
	String getMovieName();
    String getLangName();
    BigDecimal getInvestAmount();      // Use Double or BigDecimal based on your column type
    BigDecimal getReturnAmount();
    String getStatus();
    String getFirstName();
    String getLastName();
    Integer getTotalShareType();
    String getPhoneNumber();
    Integer getTotalShares();
    Integer getMovieId();	
    Integer getUserId();
    BigDecimal getPayAmount();
    String getPayStatus();
    String getInitiatedOn();
    String getProcessedOn();	
    int getPerShareAmount();
    String getShareTypeName();
    String getRemarks();
    


}
