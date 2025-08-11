package com.riseup.flimbit.workers;

import java.util.List;

import com.riseup.flimbit.entity.MovieProfitSummary;
import com.riseup.flimbit.entity.UserPayoutInitiation;
import com.riseup.flimbit.entity.dto.MovieInvestmentSummaryDTO;

public class UserPayoutInititiatorFailedWorker implements Runnable {
	
	int movieId;
	UserPayoutInitiation payout;
	MovieInvestmentSummaryDTO movieInvestment;
	MovieProfitSummary movie;
	UserPayoutInitiatorFailedTask userPayoutInitiatorFailedTask;
	
	
	UserPayoutInititiatorFailedWorker(int movieId, UserPayoutInitiation payout
			,MovieInvestmentSummaryDTO movieInvestment,
			MovieProfitSummary movie
			,UserPayoutInitiatorFailedTask userPayoutInitiatorFailedTask)
	{
		this.movieId = movieId;
		this.payout = payout;
		this.movieInvestment = movieInvestment;
		this.movie  = movie;
		this.userPayoutInitiatorFailedTask = userPayoutInitiatorFailedTask;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		 userPayoutInitiatorFailedTask.startUserPayoutInitiatorFailedTask(movieId, payout, movieInvestment, movie);
		
	}

}
