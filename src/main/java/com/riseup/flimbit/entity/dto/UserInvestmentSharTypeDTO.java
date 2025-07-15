package com.riseup.flimbit.entity.dto;

import java.math.BigDecimal;

public interface UserInvestmentSharTypeDTO {
	
	int getId();
    int getUserId();
    Long getMovieId();
    int getNumberOfShares();
    BigDecimal getAmountInvested();
    String getInvestedAt();
    String getStatus();
    String getUpdatedDate();
    BigDecimal getReturnAmount();
    String getShareTypeName(); 
    int getPricePerShare();
    int getShareId();

}
