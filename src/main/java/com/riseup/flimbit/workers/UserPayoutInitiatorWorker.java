package com.riseup.flimbit.workers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riseup.flimbit.entity.MovieProfitSummary;

public class UserPayoutInitiatorWorker implements Runnable {
	Logger logger = LoggerFactory.getLogger(UserPayoutInitiatorWorker.class);

	
	private final int userId;
    private final int movieId;
    private final UserPayoutInitiatorTask payoutInitiateProcessor;
    private final MovieProfitSummary movieProfit;

    
    public UserPayoutInitiatorWorker(int userId, int movieId, UserPayoutInitiatorTask payoutProcessor,MovieProfitSummary movieProfit) {
        this.userId = userId;
        this.movieId = movieId;
        this.payoutInitiateProcessor = payoutProcessor;
        this.movieProfit = movieProfit;
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
	    logger.info("Running worker for user: " + userId + ", movie: " + movieId);

		 this.payoutInitiateProcessor.processPayoutForUser(userId, movieId,movieProfit);
		
		
	}
	
	

}
