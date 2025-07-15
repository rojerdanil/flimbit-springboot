package com.riseup.flimbit.entity.dto;

import java.math.BigDecimal;

public interface UserInvestmentSectionDTO {
	
String getMovieName();
    
    String getLangName();
    BigDecimal getInvestAmount();
    BigDecimal getReturnAmount();
    String getStatus();
    String getFirstName();
    String getLastName(); 
    int getTotalShareType();
    String getPhoneNumber();
    int getTotalShares();
    int getMovieId();
    int getUserId();

}
