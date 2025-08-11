package com.riseup.flimbit.service;

import java.util.List;

import com.riseup.flimbit.entity.JobHolder;
import com.riseup.flimbit.entity.MoviePayoutStatusHistory;
import com.riseup.flimbit.entity.MovieProfitSummary;
import com.riseup.flimbit.entity.dto.MoviePayoutStatusHistoryDTO;
import com.riseup.flimbit.entity.dto.MovieProfitRawDataDTO;
import com.riseup.flimbit.repository.MovieProfitSummaryRepository;
import com.riseup.flimbit.request.JopRequest;
import com.riseup.flimbit.request.MoviePayoutStatusRequest;
import com.riseup.flimbit.request.MovieProfitRequest;
import com.riseup.flimbit.response.MovieInvestmentShareResposne;
import com.riseup.flimbit.response.MovieInvestmentSummaryResposne;
import com.riseup.flimbit.response.dto.MovieProfitSummaryLastHistoryStatus;

public interface MovieProfitService {
    MovieInvestmentShareResposne calculateMovieProfit(MovieProfitRequest request);
    
    MovieInvestmentSummaryResposne readMovieInfoById(int id);
    
    MovieProfitSummaryLastHistoryStatus  getMovieProfitDistributeWithLastHistory(int movieId);

    MovieProfitSummary getInitiateMoviePayout(MovieProfitRequest request);
    
    List<MoviePayoutStatusHistoryDTO>  getMoviePayoutHistory(int id);
    
    MoviePayoutStatusHistory updateStatus(MoviePayoutStatusRequest moviePayoutRequest );
    
    String  createNewJobHolder(JopRequest jobrequest);

}	
