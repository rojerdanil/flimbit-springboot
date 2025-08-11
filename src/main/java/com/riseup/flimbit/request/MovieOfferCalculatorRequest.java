package com.riseup.flimbit.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter

public class MovieOfferCalculatorRequest {
	int movieId; 
	int numberOfShares; 
	int shareTypeId;
	String promoCode;

}
