package com.riseup.flimbit.workers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.MovieProfitPayoutStatus;
import com.riseup.flimbit.constant.SchedularNames;
import com.riseup.flimbit.entity.AdminUser;
import com.riseup.flimbit.entity.MovieInvestment;
import com.riseup.flimbit.entity.MoviePayoutStatusHistory;
import com.riseup.flimbit.entity.MovieProfitSummary;
import com.riseup.flimbit.entity.User;
import com.riseup.flimbit.entity.dto.UserPayoutInitateStatusSummaryDTO;
import com.riseup.flimbit.repository.AdminUserRepository;
import com.riseup.flimbit.repository.JobLockRepository;
import com.riseup.flimbit.repository.MovieInvestRepository;
import com.riseup.flimbit.repository.MoviePayoutStatusHistoryRepository;
import com.riseup.flimbit.repository.MovieProfitSummaryRepository;
import com.riseup.flimbit.repository.UserPayoutInitiationRepository;

import jakarta.transaction.Transactional;

@Service
public class MovieMoneyPayoutInitiationJob {
	
	Logger logger = LoggerFactory.getLogger(MovieMoneyPayoutInitiationJob.class);

	 @Autowired
	 @Qualifier("sharedExecutor")
	 private ExecutorService payoutExecutorService;
	 
	 @Autowired
	 MovieProfitSummaryRepository  movieProfitSummaryRepo;
	 
	 @Autowired
	 MovieInvestRepository  movieInvestRepo;
	 
	 @Autowired
	 UserPayoutInitiatorTask userPayoutInitiatorTask;
	 
	 @Autowired
	 AdminUserRepository  adminUserRepo;
	 
	 @Autowired
	 JobLockService jobLockService;
	 
		@Autowired
		UserPayoutInitiationRepository userPayoutInitiationRepo;
		@Autowired
		MoviePayoutStatusHistoryRepository  movieStatusHistoryRepo;
		
	
	 
	

	
	     public void runPayoutBatch() {
		 
	        logger.info("Trying to acquire payout job lock..."  );
	        boolean updated = jobLockService.acquire(SchedularNames.PAYMENT_INITIALIZATION.name().toLowerCase());
	        if (updated) {
	            logger.info("Job already running. Skipping this run.");
	            return;
	        }
	        
	        
	       
			  try {
				  
				  logger.info("Lock acquired. Starting payout job.");

					logger.info("********** Movie money distribute job is starting **************");

					MovieProfitSummary movie = movieProfitSummaryRepo.findOnePendingMovie();
					if (movie == null) {
					    logger.info("No movie in distribute .......");
					    return;
					}
					
					
					int totalTasks = movieInvestRepo.countDistinctUserIdsByMovieIdAndIsProcessedFalse(movie.getMovieId());
					
					logger.info("Total tasks for CountDownLatch: " + totalTasks);

			        if (totalTasks == 0) {
			            logger.info("No active payout records found.");
			            return;
			        }
			        CountDownLatch latch = new CountDownLatch(totalTasks);

					

					int pageSize = 1000;
					int pageNumber = 0;
					List<Integer> userIds;

			        logger.info("Simulating long payout job...");

			       // Thread.sleep(TimeUnit.MINUTES.toMillis(3));
			        
			        


			    do {
			        int offset = pageNumber * pageSize;
			        userIds = movieInvestRepo.findDistinctUserIdsByMovieIdAndIsProcessedFalse(movie.getMovieId(), pageSize, offset);

			        logger.info("Users found: " + userIds.size());

			        for (Integer userId : userIds) {
			            logger.info("Starting for movieId " + movie.getMovieId() + " userId: " + userId);

			            
			            payoutExecutorService.submit(() -> {
		                    try {
		                    	new  
			            		UserPayoutInitiatorWorker(userId,movie.getMovieId(),userPayoutInitiatorTask,movie).run();
		                    } catch (Exception e) {
		                        logger.error("Error processing payout for user: " + userId, e);
		                    } finally {
		                        latch.countDown(); // Always count down
		                    }
		                });
			            
			           
			        }

			        pageNumber++;
			        // Optional: sleep or throttle between batches
			        // Thread.sleep(500); 

			    } while (!userIds.isEmpty());

			    // Optional: Wait for tasks to complete if needed
			    try {
		            latch.await();
		            logger.info("All payout tasks finished. Doing post-processing...");
		            UserPayoutInitateStatusSummaryDTO payoutInitSummary = userPayoutInitiationRepo
							.countUserPayoutStatusSummary(movie.getMovieId());

					int fullySuccessful = payoutInitSummary.getFullySuccessful();
					int partialFailures = payoutInitSummary.getPartialFailure();
					int fullyFailed = payoutInitSummary.getFullyFailed();

				    int totalUsers = fullySuccessful + partialFailures + fullyFailed;
				    
				    logger.info("Money Distributio Reports **** (movieId) " + movie.getMovieId() + " total count :" + totalUsers 
				    		 + " fullySuccessful "  + fullySuccessful  + " partial failure :" +partialFailures
				    		 + " fullyFailed "  + fullyFailed
				    		);
				    
				    if (fullyFailed == 0 && partialFailures == 0) {
				    	 movie.setStatus(MovieProfitPayoutStatus.SYSTEM.name().toLowerCase());
				        movie.setPaymentStatus(MovieProfitPayoutStatus.READY_FOR_PAYMENT.name().toLowerCase());
				    } else if (fullyFailed > 0) {
				    	 movie.setStatus(MovieProfitPayoutStatus.SYSTEM.name().toLowerCase());
				        movie.setPaymentStatus(MovieProfitPayoutStatus.PARTIALLY_COMPLETED.name().toLowerCase());
				    } else {
				    	 movie.setStatus(MovieProfitPayoutStatus.SYSTEM.name().toLowerCase());
				        movie.setPaymentStatus(MovieProfitPayoutStatus.IN_PROGRESS.name().toLowerCase());
				    }
				    Optional<AdminUser> sysAdminOpt =    adminUserRepo.findByNameIgnoreCase(MovieProfitPayoutStatus.SYSTEM.name());
				    
				    if(sysAdminOpt.isPresent())
				    {
				    logger.info(" Admin user is found  so inserted in History table");
				    MoviePayoutStatusHistory movieStatusHistory = new MoviePayoutStatusHistory();
					movieStatusHistory.setApproverId(sysAdminOpt.get().getId());
					movieStatusHistory.setCreatedAt(new Timestamp(System.currentTimeMillis()));
					movieStatusHistory.setMovieProfitSummaryId(movie.getId());
					movieStatusHistory.setReason("System checked Payout initiation ");
					movieStatusHistory.setStatus(movie.getPaymentStatus());
					  movieStatusHistoryRepo.save(movieStatusHistory);
				    }
					  
					  movieProfitSummaryRepo.save(movie);

		            
			    } catch (InterruptedException e) {
		            Thread.currentThread().interrupt();
		            logger.error("Interrupted while waiting for payout tasks", e);
		        }	

		        logger.info("All payout tasks finished successfully (or with logged errors).");
				


			  }
			  catch (Exception e) {
			        logger.error("Error during payout job", e);
			    } finally {
			    	jobLockService.release("payout_job");
			        logger.info("Payout job lock released.");
			    }

		        

			// DO NOT call shutdown() here unless this executor is not reused
			// payoutExecutorService.shutdown();

			    
	logger.info("********** Movie money distribute job is ends **************");
			    

}
	
	 
	 
}