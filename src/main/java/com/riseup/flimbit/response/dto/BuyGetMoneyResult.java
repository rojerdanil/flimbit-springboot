package com.riseup.flimbit.response.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class BuyGetMoneyResult {
	private int totalSharesWithOffer;
    private BigDecimal totalPayableAmount;
    private int freeShare;

}
