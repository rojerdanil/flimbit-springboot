package com.riseup.flimbit.workers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.riseup.flimbit.constant.MovieProfitPayoutStatus;
import com.riseup.flimbit.entity.MovieProfitSummary;
import com.riseup.flimbit.entity.UserPayoutInitiation;
import com.riseup.flimbit.entity.dto.MovieInvestmentSummaryDTO;
import com.riseup.flimbit.entity.dto.MovieProfitRawDataDTO;
import com.riseup.flimbit.repository.MovieInvestRepository;
import com.riseup.flimbit.repository.UserPayoutInitiationRepository;
import com.riseup.flimbit.response.dto.MoviePayoutDTO;

@Component
public class UserPayoutInitiatorFailedTask {
	
	Logger logger = LoggerFactory.getLogger(UserPayoutInitiatorFailedTask.class);

	@Autowired
	MovieInvestRepository movieInvestRepo;
	
	@Autowired
	UserPayoutInitiationRepository userPayoutInitRepo;
	
	@Autowired
	PayoutInitCalculator payoutCalculator;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void startUserPayoutInitiatorFailedTask(int movieId , UserPayoutInitiation payout
			,MovieInvestmentSummaryDTO movieInvestment,MovieProfitSummary movie)
	{
		logger.info("Payout initiation started FailedTask for user " + payout.getUserId() + " movieId " + movieId);
		
		try
		{
			
			
			List<MovieProfitRawDataDTO> rawDataList = movieInvestRepo.findMovieProfitDataForUser(movieId,  payout.getUserId());
		     logger.info(" Money calculate started :");
		     MovieProfitRawDataDTO  movieRawDTO = null;
				if (rawDataList != null && rawDataList.size() > 0) {
					
					for (MovieProfitRawDataDTO movieData : rawDataList) {
						
						
						if(payout.getShareTypeId() == movieData.getShareTypeId())
						{
							movieRawDTO = movieData;
						}

						
					}
					
				}
				else 
				{
					 logger.info(" MovieProfitRawDataDTO is not found:");
					 return;
				}
				if(movieRawDTO == null)
				{
					logger.info(" movieRawDTO is not found for share type id ");
					return;
				}
				
			

			MoviePayoutDTO  payoutDTO = payoutCalculator.calculatePayoutPayable(movieRawDTO, movieInvestment, movie);
			
			if(payoutDTO != null)
			{
				logger.info("Entering into new calculation");
				payout.setEligibleDate(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
				// userPayoutInit.setPayoutAmount(payoutDTO.getNetPay());
				 payout.setStatus(MovieProfitPayoutStatus.INITIATED.name().toLowerCase());
				 payout.setPaymentStatus(MovieProfitPayoutStatus.INITIATED.name().toLowerCase());

				 payout.setInitiatedOn(new Timestamp(System.currentTimeMillis()));
				
				if(payout.getPayoutAmount() != null  && payoutDTO.getNetPay() != null )
				{
					
					if(payout.getPayoutAmount().compareTo(payoutDTO.getNetPay()) == 0)
					{
						payout.setRemarks("No Pay change ");
					}
					else
					{
						payout.setRemarks("Changes in payemnt");
						payout.setPayoutAmount(payoutDTO.getNetPay());
					}
					
				}
				 userPayoutInitRepo.save(payout);

			}
			else
			{
				logger.info(" Issue in payout calcuation  ");

			}

			
                     
		}
		
		catch(Exception e)
		{
		      logger.error("Error initiating payout for movieId: {} userId: {} shareType: {}",
		                movieId, payout.getUserId(),  e);
		      String errorMsg = e.getMessage();
	           if (errorMsg != null && errorMsg.length() > 200) {
	               errorMsg = errorMsg.substring(0, 200) + "...[truncated]";
	           }
            
	           payout.setRemarks(errorMsg); // store error message

				 payout.setInitiatedOn(new Timestamp(System.currentTimeMillis()));
 
	           userPayoutInitRepo.save(payout);
			
		}

	}
	
	

}
