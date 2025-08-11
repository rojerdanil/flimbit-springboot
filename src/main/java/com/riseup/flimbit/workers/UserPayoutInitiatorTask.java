package com.riseup.flimbit.workers;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.riseup.flimbit.constant.MovieProfitPayoutStatus;
import com.riseup.flimbit.constant.PaymentStatus;
import com.riseup.flimbit.entity.MovieProfitSummary;
import com.riseup.flimbit.entity.UserPayoutInitiation;
import com.riseup.flimbit.entity.dto.MovieInvestmentSummaryDTO;
import com.riseup.flimbit.entity.dto.MovieProfitRawDataDTO;
import com.riseup.flimbit.repository.MovieInvestRepository;
import com.riseup.flimbit.repository.UserPayoutInitiationRepository;
import com.riseup.flimbit.response.dto.MoviePayoutDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserPayoutInitiatorTask {

	@Autowired
	MovieInvestRepository movieInvestRepo;
	
	@Autowired
	UserPayoutInitiationRepository userPayoutInitRepo;
	
	Logger logger = LoggerFactory.getLogger(UserPayoutInitiatorTask.class);

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void processPayoutForUser(int userId, int movieId, MovieProfitSummary movieProfit) {
		logger.info("Payout initiation started for user " + userId + " movieId " + movieId);

		List<MovieProfitRawDataDTO> rawDataList = movieInvestRepo.findMovieProfitDataForUser(movieId, userId);
		logger.info("Total share for user :" + userId + " movieId " + movieId + " total share :" + rawDataList.size());
	       logger.info(" Money calculate started :");
		if (rawDataList != null && rawDataList.size() > 0) {
			
			MovieInvestmentSummaryDTO movieInvestment = movieInvestRepo.getMovieInvestmentSummary(movieId);
			if (movieInvestment == null) {
				logger.error(" Movie and investment is not found");
				return;
			}
	 
			for (MovieProfitRawDataDTO movieData : rawDataList) {
				
				try
				{

				MoviePayoutDTO moviePayoutDTO = MoviePayoutDTO.builder()
						.platformCommision(movieData.getPlatformCommision())
						.platformCommissionAppliedShares(movieData.getPlatformCommissionAppliedShares())
						.profitCommision(movieData.getProfitCommision())
						.profitCommissionAppliedShares(movieData.getProfitCommissionAppliedShares())
						.shareTypeId(movieData.getShareTypeId()).shareTypeName(movieData.getShareTypeName())
						.totalDiscountAmount(movieData.getTotalDiscountAmount())
						.totalFreeShare(movieData.getTotalFreeShare()).totalSharesSold(movieData.getTotalSharesSold())
						.totalWalletAmount(movieData.getTotalWalletAmount())
						.profitPerShareType(
								calculateProfitPerShareType(movieProfit.getTotalProfit(), movieInvestment.getTotalInvestedAmount(),
										movieInvestment.getTotalSharesPurchased(), movieData.getTotalSharesSold()))
						.perShareProfit(calculatePerShareProfit(movieProfit.getTotalProfit(), movieInvestment.getTotalInvestedAmount(),
								movieInvestment.getTotalSharesPurchased()))
						.build();

				BigDecimal eligibleShareForPlatfComm = BigDecimal
						.valueOf(moviePayoutDTO.getTotalSharesSold()
								- moviePayoutDTO.getPlatformCommissionAppliedShares())
						.multiply(movieInvestment.getPerShareAmount());

				BigDecimal eligibleShareForProfitComm = BigDecimal
						.valueOf(
								moviePayoutDTO.getTotalSharesSold() - moviePayoutDTO.getProfitCommissionAppliedShares())
						.multiply(moviePayoutDTO.getPerShareProfit());

				moviePayoutDTO.setTotalplatFormCommission(calculateCommission(eligibleShareForPlatfComm,
						BigDecimal.valueOf(moviePayoutDTO.getPlatformCommision())));

				moviePayoutDTO.setTotalprofitCommission(calculateCommission(eligibleShareForProfitComm,
						BigDecimal.valueOf(moviePayoutDTO.getProfitCommision())));

				BigDecimal investedAmount = movieInvestment.getPerShareAmount()
						.multiply(BigDecimal.valueOf(moviePayoutDTO.getTotalSharesSold()));

				moviePayoutDTO.setNetPay(moviePayoutDTO.getProfitPerShareType()
						.subtract(moviePayoutDTO.getTotalprofitCommission()).add(investedAmount));

				 logger.info(" Net pay :(movie id) " + movieId + " user id "+ userId + " ShareType "  + movieData.getShareTypeName()  +  " : Net pay :" + moviePayoutDTO.getNetPay());
				 UserPayoutInitiation  userPayoutInit = new UserPayoutInitiation();
				 userPayoutInit.setEligibleDate(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
				 userPayoutInit.setInitiatedOn(new Timestamp(System.currentTimeMillis()));
				 userPayoutInit.setInvestmentId(movieData.getInvestmentId());
				 userPayoutInit.setMovieId(movieId);
				 userPayoutInit.setPayoutAmount(moviePayoutDTO.getNetPay());
				 userPayoutInit.setShareTypeId(moviePayoutDTO.getShareTypeId());
				 userPayoutInit.setStatus(MovieProfitPayoutStatus.INITIATED.name().toLowerCase());
				 userPayoutInit.setPaymentStatus(MovieProfitPayoutStatus.INITIATED.name().toLowerCase());
				 userPayoutInit.setUserId(userId);
				 userPayoutInitRepo.save(userPayoutInit);
				}
				catch(Exception e)
				{
				      logger.error("Error initiating payout for movieId: {} userId: {} shareType: {}",
				                movieId, userId, movieData.getShareTypeName(), e);

			           // Save the failed initiation with remarks for retry
			           UserPayoutInitiation failedPayout = new UserPayoutInitiation();
			           failedPayout.setEligibleDate(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
			           failedPayout.setInitiatedOn(new Timestamp(System.currentTimeMillis()));
			           failedPayout.setInvestmentId(movieData.getInvestmentId());
			           failedPayout.setMovieId(movieId);
			           failedPayout.setPayoutAmount(BigDecimal.ZERO); // No amount calculated due to failure
			           failedPayout.setShareTypeId(movieData.getShareTypeId());
			           failedPayout.setStatus(MovieProfitPayoutStatus.FAILED.name().toLowerCase()); // custom status for retry logic
			           failedPayout.setPaymentStatus(MovieProfitPayoutStatus.INITIATED.name().toLowerCase());
			           failedPayout.setUserId(userId);
			           
			           String errorMsg = e.getMessage();
			           if (errorMsg != null && errorMsg.length() > 200) {
			               errorMsg = errorMsg.substring(0, 200) + "...[truncated]";
			           }
                     
			           failedPayout.setRemarks(errorMsg); // store error message

			           
			           userPayoutInitRepo.save(failedPayout);
					
				}
				// userPayoutInit.setInvestmentId(movieInvestment.ge) 
				

			}

		}

	}   

	  public BigDecimal calculateCommission(BigDecimal amount, BigDecimal percentage) {
	        return amount.multiply(percentage)
	                     .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
	    }
	    public BigDecimal calculateProfitPerShareType(BigDecimal totalProfit, BigDecimal totalBudget, int totalShares, int shareCount) {
	        if (totalShares == 0) {
	            return BigDecimal.ZERO;
	        }
	        // Net profit after budget deduction
	        BigDecimal netProfit = totalProfit.subtract(totalBudget);

	        // Convert totalShares to BigDecimal for division
	        BigDecimal profitPerShare = netProfit.divide(BigDecimal.valueOf(totalShares), 2, RoundingMode.HALF_UP);

	        // Calculate profit for this share type
	        return profitPerShare.multiply(BigDecimal.valueOf(shareCount));
	    }
	    BigDecimal calculatePerShareProfit(BigDecimal totalProfit, BigDecimal totalBudget, int totalShares) {
	        if (totalShares == 0) {
	            return BigDecimal.ZERO;
	        }
	        BigDecimal netProfit = totalProfit.subtract(totalBudget);
	        return netProfit.divide(BigDecimal.valueOf(totalShares), 2, RoundingMode.HALF_UP);
	    }
}
