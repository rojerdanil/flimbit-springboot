package com.riseup.flimbit.workers;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.MovieProfitSummary;
import com.riseup.flimbit.entity.dto.MovieInvestmentSummaryDTO;
import com.riseup.flimbit.entity.dto.MovieProfitRawDataDTO;
import com.riseup.flimbit.response.dto.MoviePayoutDTO;

@Service
public class PayoutInitCalculator {
	
	public MoviePayoutDTO calculatePayoutPayable(MovieProfitRawDataDTO movieData,MovieInvestmentSummaryDTO movieInvestment,MovieProfitSummary movieProfit)
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
		
		return moviePayoutDTO;
		
		
	}
	
	 private BigDecimal calculateCommission(BigDecimal amount, BigDecimal percentage) {
	        return amount.multiply(percentage)
	                     .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
	    }
	 private BigDecimal calculateProfitPerShareType(BigDecimal totalProfit, BigDecimal totalBudget, int totalShares, int shareCount) {
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
	 private   BigDecimal calculatePerShareProfit(BigDecimal totalProfit, BigDecimal totalBudget, int totalShares) {
	        if (totalShares == 0) {
	            return BigDecimal.ZERO;
	        }
	        BigDecimal netProfit = totalProfit.subtract(totalBudget);
	        return netProfit.divide(BigDecimal.valueOf(totalShares), 2, RoundingMode.HALF_UP);
	    }

}
