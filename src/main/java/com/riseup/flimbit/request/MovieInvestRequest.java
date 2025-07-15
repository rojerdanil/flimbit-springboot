package com.riseup.flimbit.request;

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
}
