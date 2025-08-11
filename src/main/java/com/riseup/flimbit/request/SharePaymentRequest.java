package com.riseup.flimbit.request;

import java.math.BigDecimal;

import com.riseup.flimbit.response.dto.OfferMoneyResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class SharePaymentRequest {
	int movieId; 
	int numberOfShares; 
	int shareTypeId;
	String promoCode;
    private BigDecimal totalPayable;
	String paymentMethod;
	
	OfferMoneyResponse  offerMoneyResponse;
	

}
