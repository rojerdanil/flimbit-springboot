package com.riseup.flimbit.response.dto;

import java.sql.Timestamp;

import com.riseup.flimbit.entity.MovieProfitSummary;
import com.riseup.flimbit.entity.dto.MoviePayoutStatusHistoryDTO;
import com.riseup.flimbit.entity.dto.UserPayoutInitateStatusSummaryDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class MovieProfitSummaryLastHistoryStatus {
	
	MovieProfitSummary  movieProfitSummary;
	
	MoviePayoutStatusHistoryDTO  lastHistory;
	
	UserPayoutInitateStatusSummaryDTO userPayoutIniateSummary; 
	
	

}
