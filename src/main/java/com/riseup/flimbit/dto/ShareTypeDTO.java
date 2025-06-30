package com.riseup.flimbit.dto;

import java.sql.Timestamp;


public interface ShareTypeDTO {
	
	
		     Long getId();

	     Integer getCategoryId();

	     String getName();
	     String getStartDate();
	     String getEndDate();
	     String getPricePerShare();
	     String getCompanyCommissionPercent();
	     String getProfitCommissionPercent();
	     Integer getNumberOfShares();
	      Boolean getIsActive();
	     String getCreatedDate(); 
	     String getUpdatedDate();
	     Long getMovieId();	     
	     Integer getSoldShare();
	     Integer getSoldAmount();
	     Double getCompanyCommission();
	     Double getCompanyProfitCommission();
	     Integer getBudget();
	     Integer getTotalShare();
	     Integer getPerShareAmount();

}
