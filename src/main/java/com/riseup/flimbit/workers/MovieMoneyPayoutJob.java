package com.riseup.flimbit.workers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.MovieProfitPayoutStatus;
import com.riseup.flimbit.constant.PaymentStatus;
import com.riseup.flimbit.constant.PayoutStatus;
import com.riseup.flimbit.constant.SchedularNames;
import com.riseup.flimbit.entity.AdminUser;
import com.riseup.flimbit.entity.JobHolder;
import com.riseup.flimbit.entity.MoviePayoutStatusHistory;
import com.riseup.flimbit.entity.MovieProfitSummary;
import com.riseup.flimbit.entity.UserPayoutInitiation;
import com.riseup.flimbit.entity.dto.MovieInvestmentSummaryDTO;
import com.riseup.flimbit.entity.dto.UserPayoutInitateStatusSummaryDTO;
import com.riseup.flimbit.repository.AdminUserRepository;
import com.riseup.flimbit.repository.JobHolderRepository;
import com.riseup.flimbit.repository.MovieInvestRepository;
import com.riseup.flimbit.repository.MoviePayoutStatusHistoryRepository;
import com.riseup.flimbit.repository.MovieProfitSummaryRepository;
import com.riseup.flimbit.repository.PayoutRepository;
import com.riseup.flimbit.repository.UserPayoutInitiationRepository;

@Service
public class MovieMoneyPayoutJob {
	Logger logger = LoggerFactory.getLogger(MovieMoneyPayoutJob.class);
	 @Autowired
	 @Qualifier("sharedExecutor")
	 private ExecutorService payoutExecutorService;
	 
	 @Autowired
	 JobLockService jobLockService;
	@Autowired
	UserPayoutInitiationRepository userPayoutInitRepo;
	
  @Autowired
  UserPayoutTask userPayoutTask;
	
	@Autowired
	MovieProfitSummaryRepository movieProfitSummaryRepo;

		@Autowired
		JobHolderRepository jobRepo;
		
		@Autowired
		PayoutRepository payoutRepo;
		
		@Autowired
		MoviePayoutStatusHistoryRepository movieStatusHistoryRepo;
		
		@Autowired
		AdminUserRepository adminUserRepo;
		
		@Autowired
		UserPayoutInitiationRepository userPayoutInitiationRepo;

		
		public void runJobs() {
		    logger.info("...Trying to acquire MovieMoneyPayoutJob lock...");
		    boolean alreadyRunning = jobLockService.acquire(SchedularNames.PAYMENT_APPROVED.name().toLowerCase());
		    if (alreadyRunning) {
		        logger.info("Job already running. Skipping this run.");
		        return;
		    }

		    try {
		        Optional<JobHolder> paymentJobOpt = jobRepo.findNextActiveJobByJobName(
		                SchedularNames.PAYMENT_APPROVED.name().toLowerCase());
		        if (!paymentJobOpt.isPresent()) {
		            logger.info("No active jobs data in table");
		            return;
		        }

		        JobHolder paymentJob = paymentJobOpt.get();
		        logger.info("Data picking from Payout starts *****");

		        MovieProfitSummary movie = movieProfitSummaryRepo.findByMovieId(paymentJob.getMovieId());
		        if (movie == null) {
		            logger.info("No movie in distribute .......");
		            return;
		        }
		        if (!movie.getStatus().equalsIgnoreCase(MovieProfitPayoutStatus.APPROVED.name())) {
		            logger.info("Movie profit summary is not approved ... ");
		            return;
		        }
		        if (movie.getStatus().equalsIgnoreCase(PayoutStatus.COMPLETED.name())) {
		            logger.info("Movie profit summary payout is already done ... ");
		            return;
		        }
		        if (!movie.getPaymentStatus().equalsIgnoreCase(MovieProfitPayoutStatus.READY_FOR_PAYMENT.name())) {
		            logger.info("Movie profit summary payout is not READY_FOR_PAYMENT ... ");
		            return;
		        }

		        // Count total tasks in one DB call
		        int totalTasks = userPayoutInitRepo.countUserPayoutBymovIdandStatus(
		                paymentJob.getMovieId(),
		                MovieProfitPayoutStatus.INITIATED.name().toLowerCase()
		        );
		        logger.info("Total tasks for CountDownLatch: " + totalTasks);

		        if (totalTasks == 0) {
		            logger.info("No active payout records found.");
		            return;
		        }

		        CountDownLatch latch = new CountDownLatch(totalTasks);

		        int pageNumber = 0;
		        int pageSize = 100;
		        Page<UserPayoutInitiation> page;
		        logger.info("Simulating MovieMoneyPayoutJob...");

		        do {
		            Pageable pageable = PageRequest.of(pageNumber, pageSize);
		            page = userPayoutInitRepo.findUserPayoutBymovIdandStatus(
		                    paymentJob.getMovieId(),
		                    MovieProfitPayoutStatus.INITIATED.name().toLowerCase(),
		                    pageable
		            );
		            List<UserPayoutInitiation> payoutRecords = page.getContent();

		            logger.info("Records found: " + payoutRecords.size());

		            for (UserPayoutInitiation userPayoutInit : payoutRecords) {
		                payoutExecutorService.submit(() -> {
		                    try {
		                        new UserPayoutWorker(userPayoutInit, userPayoutTask).run();
		                    } catch (Exception e) {
		                        logger.error("Error processing payout for user: " + userPayoutInit.getUserId(), e);
		                    } finally {
		                        latch.countDown(); // Always count down
		                    }
		                });
		            }

		            pageNumber++;
		        } while (!page.isLast() && !page.getContent().isEmpty());

		        // Wait until all tasks finish
		        try {
		            latch.await();
		            logger.info("All payout tasks finished. Doing post-processing...");

		           
		            
		           
					
					int totalInitCount =   userPayoutInitiationRepo.countUserPayoutInitBymovId(movie.getMovieId());
					// it will get only fully success because no failed in payouts
					int totalPayoutCount = payoutRepo.countUserPayoutBymovId(movie.getMovieId());

					logger.info("Money Distributio Reports **** (movieId) " + movie.getMovieId() + " total count :" + totalPayoutCount
							+ "  payout initilation count  "
							+ totalInitCount);
					
					if(totalPayoutCount == 0)
					{
						   logger.info("✅ Payment fully failed for movie {}", movie.getMovieId());
						    movie.setStatus(MovieProfitPayoutStatus.PAYMENT_FULLY_FAILED.name().toLowerCase());
					}
					
					else if (totalPayoutCount == totalInitCount) {
					    logger.info("✅ Payment fully completed for movie {}", movie.getMovieId());
					    movie.setStatus(MovieProfitPayoutStatus.PAYMENT_COMPLETED.name().toLowerCase());
					    movie.setPaymentStatus(MovieProfitPayoutStatus.PAYMENT_COMPLETED.name().toLowerCase());
					} 
					else  {
					    logger.info("✅ Payment partially  failed for movie {}", movie.getMovieId());
					    movie.setStatus(MovieProfitPayoutStatus.PAYMENT_PARTIALLY_COMPLETED.name().toLowerCase());
					    //movie.setPaymentStatus(MovieProfitPayoutStatus.PAYMENT_FULLY_FAILED.name().toLowerCase());
					} 
				
					
					Optional<AdminUser> sysAdminOpt = adminUserRepo.findByNameIgnoreCase(MovieProfitPayoutStatus.SYSTEM.name());

					if (sysAdminOpt.isPresent()) {
						logger.info(" Admin user is found  so inserted in History table");
						MoviePayoutStatusHistory movieStatusHistory = new MoviePayoutStatusHistory();
						movieStatusHistory.setApproverId(sysAdminOpt.get().getId());
						movieStatusHistory.setCreatedAt(new Timestamp(System.currentTimeMillis()));
						movieStatusHistory.setMovieProfitSummaryId(movie.getId());
						movieStatusHistory.setReason(movie.getStatus());
						movieStatusHistory.setStatus(movie.getPaymentStatus());
						movieStatusHistoryRepo.save(movieStatusHistory);
					}

					movieProfitSummaryRepo.save(movie);
					 paymentJob.setStatus(PayoutStatus.COMPLETED.name().toLowerCase());
					 paymentJob.setJobEndDate(new Timestamp(System.currentTimeMillis()));
					 jobRepo.save(paymentJob);
		            
		            
		            
		            
		        } catch (InterruptedException e) {
		            Thread.currentThread().interrupt();
		            logger.error("Interrupted while waiting for payout tasks", e);
		        }	

		        logger.info("All payout tasks finished successfully (or with logged errors).");

		    } catch (Exception e) {
		        logger.error("Error during payout job", e);
		    } finally {
		        jobLockService.release(SchedularNames.PAYMENT_APPROVED.name().toLowerCase());
		        logger.info("Payout job lock released.");
		    }

		    logger.info("********** MovieMoneyPayoutJob ends **************");
		}


}
