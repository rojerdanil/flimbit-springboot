package com.riseup.flimbit.request;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MovieInvestRequest {
	int movieId; 
	int numberOfShares; 
	int shareTypeId;
	String promoCode;
	
	 BigDecimal  totalDiscountAmount;
	 BigDecimal  totalWalletAmount ;
    boolean isPlatformCommision ;
    boolean isProfitCommission;
	 int totalFreeShare;
	 int existTotalShare;
	 
	 
}
