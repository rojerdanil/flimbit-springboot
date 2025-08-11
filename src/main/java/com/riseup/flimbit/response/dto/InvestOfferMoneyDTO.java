package com.riseup.flimbit.response.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class InvestOfferMoneyDTO {
	int investId;
	long offerId;
    BigDecimal  discountAmount = BigDecimal.valueOf(0);
	BigDecimal  walletAmount = BigDecimal.valueOf(0);
    boolean isNoPlatformCommision = false;
    boolean isNoProfitCommission = false;
    int freeShare;
    String offerName;

    int totalShare;    
    int movId;

}
