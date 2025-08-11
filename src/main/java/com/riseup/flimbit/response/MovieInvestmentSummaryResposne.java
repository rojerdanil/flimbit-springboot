package com.riseup.flimbit.response;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MovieInvestmentSummaryResposne {
	
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
	    
	    
	    


	    

}
