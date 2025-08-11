package com.riseup.flimbit.response.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class OfferMoneyResponse {
	List<InvestOfferMoneyDTO>  investOFferList = new ArrayList<InvestOfferMoneyDTO>();
	 BigDecimal  totalDiscountAmount = BigDecimal.valueOf(0);
	 BigDecimal  totalWalletAmount = BigDecimal.valueOf(0);
     boolean isPlatformCommision = false;
     boolean isProfitCommission = false;
	 int totalFreeShare = 0;
	 int totalShare = 0;
	 Boolean isBuyAndGet;
	 boolean isOfferAvailable;
	 BigDecimal totalOrignalAmount = BigDecimal.valueOf(0);
	 BigDecimal perShareAmount = BigDecimal.valueOf(0);
	 boolean isUserReachedMaxInvest;
	 BigDecimal newTotalAfterOffer = BigDecimal.valueOf(0);
	 BigDecimal maxInvestAmount = BigDecimal.valueOf(0);
	 BigDecimal userAlreadyInvested = BigDecimal.valueOf(0);
	 boolean isOfferGlobleEnable = false;

	 
	 

	 
}
