package com.riseup.flimbit.entity.dto;

import java.math.BigDecimal;

public interface MovieProfitRawDataDTO {

	int getShareTypeId();
    String getShareTypeName();
    int getTotalSharesSold();
    int getPlatformCommissionAppliedShares();
    int getProfitCommissionAppliedShares();
    BigDecimal getTotalDiscountAmount();
    BigDecimal getTotalWalletAmount();
    int getPlatformCommision();
    int getProfitCommision();
    int getTotalFreeShare();
    int getInvestmentId();
}
