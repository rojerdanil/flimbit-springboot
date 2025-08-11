package com.riseup.flimbit.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PaymentFeeResponse {
	
	    private BigDecimal investmentAmount;
	    private BigDecimal convenienceFee;
	    private BigDecimal gst;
	    private BigDecimal totalPayable;
	    private String paymentMethod;

}
