package com.riseup.flimbit.request;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Setter
@Getter
public class MovieIinvestSuccess {
	int movieId; 
	int numberOfNewShares; 
	BigDecimal perShareAmount;
	int numberOfOldShares;
	BigDecimal oldShareAmount;
	
	
}
