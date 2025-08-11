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
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.MovieProfitPayoutStatus;
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
import com.riseup.flimbit.repository.JobLockRepository;
import com.riseup.flimbit.repository.MovieInvestRepository;
import com.riseup.flimbit.repository.MoviePayoutStatusHistoryRepository;
import com.riseup.flimbit.repository.MovieProfitSummaryRepository;
import com.riseup.flimbit.repository.UserPayoutInitiationRepository;

@Service
public class MovieMoneyInitiationFailedJob {
	Logger logger = LoggerFactory.getLogger(MovieMoneyPayoutInitiationJob.class);

	@Autowired
	@Qualifier("sharedExecutor")
	private ExecutorService payoutExecutorService;

	@Autowired
	JobLockService jobLockService;

	@Autowired
	JobHolderRepository jobRepo;

	@Autowired
	UserPayoutInitiationRepository userPayoutInitRepo;

	@Autowired
	MovieProfitSummaryRepository movieProfitSummaryRepo;
	@Autowired
	UserPayoutInitiationRepository userPayoutInitiationRepo;

	@Autowired
	MovieInvestRepository movieInvestRepo;

	@Autowired
	AdminUserRepository adminUserRepo;

	@Autowired
	MoviePayoutStatusHistoryRepository movieStatusHistoryRepo;
	
	@Autowired
	UserPayoutInitiatorFailedTask userPayoutInitiatorFailedTask;

	public void runPayoutInitationFailedBatch() {
		logger.info("********** Movie money MoneyDistributorFailedJob job is starting **************");

		
        boolean updated = jobLockService.acquire(SchedularNames.PAYMENT_INITIALIZATION_FAILLED_RECALL.name().toLowerCase());
		if (updated) {
			logger.info("Job already running. Skipping this run.");
			return;
		}
	
		try {

			logger.info("Trying to acquire payout job lock...");
		

			logger.info("********** Movie money MoneyDistributorFailedJob job crossed locks **************");

			Optional<JobHolder> failedOpt = jobRepo.findNextActiveJobByJobName(SchedularNames.PAYMENT_INITIALIZATION_FAILLED_RECALL.name().toLowerCase());

			if (!failedOpt.isPresent()) {
				logger.info("No active jobs data in table");
				return;
			}
			logger.info(" data picking from Failed user initiation starts *****");
			JobHolder faiedInit = failedOpt.get();

			int pageNumber = 0;
			int pageSize = 100; // batch size
			Page<UserPayoutInitiation> page;
			
			logger.info("Simulating long payout job...");

			MovieInvestmentSummaryDTO movieInvestment = movieInvestRepo
					.getMovieInvestmentSummary(faiedInit.getMovieId());
			if (movieInvestment == null) {
				logger.error(" Movie and investment is not found");
				return;
			}

			MovieProfitSummary movie = movieProfitSummaryRepo.findByMovieId(faiedInit.getMovieId());
			if (movie == null) {
				logger.info("No movie in distribute .......");
				return;
			}

			int totalTasks =    userPayoutInitRepo.countFailedByMovieIdIgnoreCase(faiedInit.getMovieId());
			
			if(totalTasks == 0)
			{
				logger.info("No failed records");
				return;
			}
			
	        CountDownLatch latch = new CountDownLatch(totalTasks);


			do {

				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				page = userPayoutInitRepo.findFailedByMovieIdIgnoreCase(faiedInit.getMovieId(), pageable);

				List<UserPayoutInitiation> payoutRecords = page.getContent();

				logger.info("Failed payout records found: " + payoutRecords.size());
                 
				if(payoutRecords == null || payoutRecords.size() == 0)
				{
					logger.info("No failed records");
					return;
				}
				for(UserPayoutInitiation userPayoutInit : payoutRecords)
				{
					
					
					try {
						new UserPayoutInititiatorFailedWorker(faiedInit.getMovieId(),
								userPayoutInit,
								movieInvestment, 
								movie,
								userPayoutInitiatorFailedTask).run();
                    } catch (Exception e) {
                        logger.error("Error processing payout for user: " + userPayoutInit.getUserId(), e);
                    } finally {
                        latch.countDown(); // Always count down
                    }
				}

				pageNumber++;

				// Optional: sleep to throttle batches
				// Thread.sleep(500);

			} while (!page.isLast() && !page.getContent().isEmpty());

			 try {
		            latch.await();
		            logger.info("All payout tasks finished successfully (or with logged errors).");

					UserPayoutInitateStatusSummaryDTO payoutInitSummary = userPayoutInitiationRepo
							.countUserPayoutStatusSummary(faiedInit.getMovieId());

					int fullySuccessful = payoutInitSummary.getFullySuccessful();
					int partialFailures = payoutInitSummary.getPartialFailure();
					int fullyFailed = payoutInitSummary.getFullyFailed();

					int totalUsers = fullySuccessful + partialFailures + fullyFailed;

					logger.info("Money Distributio Reports **** (movieId) " + movie.getMovieId() + " total count :" + totalUsers
							+ " fullySuccessful " + fullySuccessful + " partial failure :" + partialFailures + " fullyFailed "
							+ fullyFailed);
					
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
					Optional<AdminUser> sysAdminOpt = adminUserRepo.findByNameIgnoreCase(MovieProfitPayoutStatus.SYSTEM.name());

					if (sysAdminOpt.isPresent()) {
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
					faiedInit.setStatus("completed");
					faiedInit.setJobEndDate(new Timestamp(System.currentTimeMillis()));
					jobRepo.save(faiedInit);
		            
			 }
		  catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	            logger.error("Interrupted while waiting for payout tasks", e);
	        }	

	       
			

		} catch (Exception e) {
			logger.error("Error during payout failed job", e);
		} finally {
			jobLockService.release("payout_failed_job");
			logger.info("Payout job lock released.");
		}

		// DO NOT call shutdown() here unless this executor is not reused
		// payoutExecutorService.shutdown();

		logger.info("********** Movie money distribute job is ends **************");

	}

}
