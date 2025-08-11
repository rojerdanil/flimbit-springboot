package com.riseup.flimbit.response;

import com.riseup.flimbit.request.SharePaymentRequest;
import com.riseup.flimbit.response.dto.OfferMoneyResponse;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SharePaymentResponse {
	
	String orderNo;
	String status;

}
