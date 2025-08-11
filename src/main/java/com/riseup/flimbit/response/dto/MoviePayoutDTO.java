package com.riseup.flimbit.response.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MoviePayoutDTO {
	
	int shareTypeId;
    String shareTypeName;
    int totalSharesSold;
    int platformCommissionAppliedShares;
    int profitCommissionAppliedShares;
    BigDecimal totalDiscountAmount;
    BigDecimal totalWalletAmount;
    int platformCommision;
    int profitCommision;
    int totalFreeShare;
    BigDecimal profitPerShareType;
    BigDecimal perShareProfit;
    BigDecimal totalplatFormCommission;
    BigDecimal totalprofitCommission;
    BigDecimal netPay;
    

}
