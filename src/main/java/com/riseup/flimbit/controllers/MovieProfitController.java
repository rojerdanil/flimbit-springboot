package com.riseup.flimbit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.request.JopRequest;
import com.riseup.flimbit.request.MoviePayoutStatusRequest;
import com.riseup.flimbit.request.MovieProfitRequest;
import com.riseup.flimbit.service.MovieProfitService;
import com.riseup.flimbit.utility.HttpResponseUtility;
import com.riseup.flimbit.workers.MovieMoneyInitiationFailedJob;
import com.riseup.flimbit.workers.MovieMoneyPayoutInitiationJob;
import com.riseup.flimbit.workers.MovieMoneyPayoutJob;


@RestController
@RequestMapping("/movie-profit/")
public class MovieProfitController {
	@Autowired
    MovieProfitService movieProfitService;
	
	@Autowired
	MovieMoneyPayoutInitiationJob movieDistJob;
	
	@Autowired
	MovieMoneyPayoutJob movieMoneyPayoutJob;
	
	@Autowired
	MovieMoneyInitiationFailedJob moneyDistributorFailedJob;
	
	   @PostMapping("/calculate")
	    public ResponseEntity<?> calculateProfit(@RequestBody MovieProfitRequest request) {
	        return HttpResponseUtility.getHttpSuccess(movieProfitService.calculateMovieProfit(request));
	    }
	   
	   @GetMapping("/investment-summary/{id}")
	    public ResponseEntity<?> getMovieInvestmentSummary(@PathVariable int id) {
	        return HttpResponseUtility.getHttpSuccess(movieProfitService.readMovieInfoById(id));

	    }
	   @GetMapping("/movieProfitStatus/{id}")
	    public ResponseEntity<?> getMovieProfitStatus(@PathVariable int id) {
	        return HttpResponseUtility.getHttpSuccess(movieProfitService.getMovieProfitDistributeWithLastHistory(id));

	    }
	   
	   @PostMapping("/initiateMoviePayout")
	    public ResponseEntity<?> initiateMoviePayout(@RequestBody MovieProfitRequest request) {
	        return HttpResponseUtility.getHttpSuccess(movieProfitService.getInitiateMoviePayout(request));
	    }
	   
	   @GetMapping("/movieProfitStatusHistory/{id}")
	    public ResponseEntity<?> getMovieProfitStatusHistory(@PathVariable int id) {
	        return HttpResponseUtility.getHttpSuccess(movieProfitService.getMoviePayoutHistory(id));

	    }
	   @PostMapping("/updateStatus")
	    public ResponseEntity<?> updateStatus(@RequestBody MoviePayoutStatusRequest request) {
	        return HttpResponseUtility.getHttpSuccess(movieProfitService.updateStatus(request));
	    }
	   
	   @GetMapping("/startDistributeJob")
	    public ResponseEntity<?> getRunBatch() {
		 
			  //  new Thread(() -> movieDistJob.runPayoutBatch()).start();
		   new Thread(() -> movieMoneyPayoutJob.runJobs()).start();
			    
			    
			
	        return HttpResponseUtility.getHttpSuccess("success");

	    }

	   @PostMapping("/resheduleProfitSummaryFailure")
	    public ResponseEntity<?> getResheduleProfitSummaryFailure(@RequestBody JopRequest jobrequest) {
	        return HttpResponseUtility.getHttpSuccess(movieProfitService.createNewJobHolder(jobrequest));

	    }

}
