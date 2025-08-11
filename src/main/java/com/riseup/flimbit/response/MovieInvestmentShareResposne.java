package com.riseup.flimbit.response;

import java.math.BigDecimal;
import java.util.List;

import com.riseup.flimbit.response.dto.MoviePayoutDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
public class MovieInvestmentShareResposne {
	
	     int movieId;
	    String movieName;
	    BigDecimal totalInvestedAmount;
	    Integer totalSharesPurchased;
	    Integer totalInvestors;
	    int totalFreeShare;
	    int totalShareTypeShare;
	    BigDecimal totalDiscount	;
	    BigDecimal totalWalletAmount;
	    BigDecimal budget;
	    BigDecimal perShareAmount;
	    List<MoviePayoutDTO> listSharePayout;

}
