package com.riseup.flimbit.utility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.ConfigCacheService;
import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.constant.StatusEnum;
import com.riseup.flimbit.controllers.LanguageController;
import com.riseup.flimbit.entity.Movie;
import com.riseup.flimbit.entity.SystemSettings;
import com.riseup.flimbit.entity.dto.MovieOfferFlatDto;
import com.riseup.flimbit.repository.MovieInvestRepository;
import com.riseup.flimbit.repository.MovieShareOfferMapRepository;
import com.riseup.flimbit.repository.MoviesRepository;
import com.riseup.flimbit.repository.SystemSettingsRepository;
import com.riseup.flimbit.request.MovieInvestRequest;
import com.riseup.flimbit.request.MovieOfferCalculatorRequest;
import com.riseup.flimbit.response.dto.BuyGetMoneyResult;
import com.riseup.flimbit.response.dto.InvestOfferMoneyDTO;
import com.riseup.flimbit.response.dto.OfferMoneyResponse;

@Service
public class OfferMoneyCalculator {

	@Autowired
	MovieShareOfferMapRepository movieShareOffer;
	
	Logger logger
    = LoggerFactory.getLogger(OfferMoneyCalculator.class);
	
	@Autowired
	MoviesRepository movieRepository;
	
	@Autowired
	SystemSettingsRepository  systemRepository;
	
	@Autowired
	MovieInvestRepository movieInvestRepo;
	
	 @Autowired
	 ConfigCacheService  configCacheService;

	
	
	public OfferMoneyResponse  getoOfferAmountForMovieAndShareTypeId(MovieOfferCalculatorRequest movieRequest,boolean isInvestOfferMoneyNeed,int userId)
	{
		
		// first get read all active offers in today date
		//
		int movId = movieRequest.getMovieId();
		int shareTypeId = movieRequest.getShareTypeId();
		String promoCode = movieRequest.getPromoCode();
		int numberOfShare = movieRequest.getNumberOfShares();
		logger.info("****** Calcuate offer money starts ******** " + movId + " : " 
				+ " : " +promoCode + " " + numberOfShare + " " );  	
		
		
	   List<Integer> afferctOfferList = new ArrayList<Integer>();
	   BigDecimal  totalDiscountAmount = BigDecimal.valueOf(0);
	   BigDecimal  totalWalletAmount = BigDecimal.valueOf(0);
       boolean isPlatformCommision = false;
       boolean isProfitCommission = false;
       boolean  isBuyAndGet = false;
	   int totalFreeShare = 0;
	   
	  Movie movie = movieRepository.findById(Long.valueOf(movieRequest.getMovieId()))
			         .orElseThrow(() -> new RuntimeException("Movie is not found " + movieRequest.getMovieId()));
	   
	   
	   
		
		
		List<InvestOfferMoneyDTO>  investOFferList = new ArrayList<InvestOfferMoneyDTO>();
		
		
		 Optional<SystemSettings> sysSettingOPt   =	systemRepository.findByKeyIgnoreCaseAndGroupNameIgnoreCase("offer_active", EntityName.OFFER.name());

		 boolean isOfferGlobleEnable =  sysSettingOPt.isPresent() && sysSettingOPt.get().getValue().equalsIgnoreCase(StatusEnum.ACTIVE.name())
				                        ? true : false;
		 logger.info("Is offer disabled offer_active  " + isOfferGlobleEnable);

		 boolean isOfferAvailable = false;
		
		 
		if(isOfferGlobleEnable)
		{
			logger.info("Global offer is entering  ");
			List<MovieOfferFlatDto>  offersList	  =   movieShareOffer.getOfferForMovieAndShareType(movId,shareTypeId);
			logger.info("offersList " + offersList.size());


	     for(MovieOfferFlatDto movieOffer : offersList )
	     {
	    	 BuyGetMoneyResult buyGetResult = null;
	    	 boolean isPromoCodeVerifed = true; 
	    	if((movieOffer.getMaxUsers() == -1 || movieOffer.getMaxUsers() > 0) && isPromoCodeVerifed) 
	    	{
		    	 InvestOfferMoneyDTO investOFferMoney = InvestOfferMoneyDTO.builder().build();

		         boolean hasValue = false;
		         investOFferMoney.setOfferId(movieOffer.getOfferId());
		         investOFferMoney.setOfferName(movieOffer.getOfferName());
		    	 
		    	 if(movieOffer.getIsBuyAndGet())
	    	 {
	    		 buyGetResult = calculateOfferPriceWithTotalShares(BigDecimal.valueOf(movieOffer.getPricePerShare())
	    				 ,numberOfShare,movieOffer.getBuyQuantity()
	    				 ,movieOffer.getfreeQuantity()) ;
	    		 
	    		 totalFreeShare = totalFreeShare + buyGetResult.getFreeShare();
	    		 
	    		 investOFferMoney.setFreeShare(buyGetResult.getFreeShare());
	    		 
	    		 isBuyAndGet  = true;
	                hasValue = true;

	    
	                
	    	 }  
	    	 if (movieOffer.getDiscountAmount() != null 
	    		        && movieOffer.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
	    		    // Discount is greater than zero
	    		 totalDiscountAmount = totalDiscountAmount.add(movieOffer.getDiscountAmount());  
	    		 investOFferMoney.setDiscountAmount(movieOffer.getDiscountAmount());
	                hasValue = true;

	    		 
	    	  }
	    	 if(movieOffer.getWalletCreditAmount() != null 
	    			 && movieOffer.getWalletCreditAmount().compareTo(BigDecimal.ZERO) > 0)
	    	 {
	    		 totalWalletAmount = totalWalletAmount.add(movieOffer.getWalletCreditAmount());
	    		 investOFferMoney.setWalletAmount(movieOffer.getWalletCreditAmount());
	                hasValue = true;

	    	 }
	    	 
	    	 if(movieOffer.getNoPlatFormCommission())
	    	 {
	    		 isPlatformCommision = true;
	    		 investOFferMoney.setNoPlatformCommision(true);
	                hasValue = true;

	    		 
	    	 }
	    	 if(movieOffer.getNoProfitCommission())
	    	 {
	    		 isProfitCommission = true;
	    		 investOFferMoney.setNoProfitCommission(true);
	                hasValue = true;

	    		 
	    	 }
	    	 
	    	 if (hasValue) {
	    		 isOfferAvailable = true;
	    		 investOFferMoney.setTotalShare(movieRequest.getNumberOfShares());
	    		 investOFferMoney.setMovId(movieRequest.getMovieId());
	    		 if(isInvestOfferMoneyNeed)
	    		 investOFferList.add(investOFferMoney);
	         }
	    	}
	    	
	     }
	     
		}
		  
		   
	     
		   BigDecimal totalDiscount = investOFferList.stream()
			        .map(InvestOfferMoneyDTO::getDiscountAmount)
			        .filter(Objects::nonNull)
			        .reduce(BigDecimal.ZERO, BigDecimal::add);

			BigDecimal totalWallet = investOFferList.stream()
			        .map(InvestOfferMoneyDTO::getWalletAmount)
			        .filter(Objects::nonNull)
			        .reduce(BigDecimal.ZERO, BigDecimal::add);

			int totalFreeShareFinal = investOFferList.stream()
			        .mapToInt(InvestOfferMoneyDTO::getFreeShare)
			        .sum();
		   
		logger.info("Verify amounts");
		
		logger.info(" total freeshare : " + totalFreeShareFinal  + " : all " +totalFreeShare);
		logger.info(" total Wallet : " + totalWallet  + " : all " +totalWalletAmount);
		logger.info(" total Discount : " + totalDiscountAmount  + " : all " +totalDiscount);
		logger.info(" isProfitCommissione : " + isProfitCommission  );
		logger.info("isProfitCommission : " + isProfitCommission );
		
		OfferMoneyResponse offerMoney = OfferMoneyResponse.builder()
				.investOFferList(investOFferList)
				.totalDiscountAmount(totalDiscountAmount)
                .totalFreeShare(totalFreeShare)	
                .totalWalletAmount(totalWalletAmount)
                .isPlatformCommision(isPlatformCommision)
                .isProfitCommission(isProfitCommission)
                .totalShare(numberOfShare)
                .isBuyAndGet(isBuyAndGet)
                .totalOrignalAmount(BigDecimal.valueOf(   movie.getPerShareAmount() * movieRequest.getNumberOfShares()))
                .perShareAmount(BigDecimal.valueOf(movie.getPerShareAmount()))
                .isOfferAvailable(isOfferAvailable)
                .isOfferGlobleEnable(isOfferGlobleEnable)
                 .build();
		
		         offerMoney.setNewTotalAfterOffer(calculatePriceAfterOfferDiscount(offerMoney.getTotalOrignalAmount(),offerMoney.getTotalDiscountAmount()));
		// calculate  orginal price after discount and everything
		        
		        
		         BigDecimal userAlreadyInvested = movieInvestRepo.getTotalInvestedAmountCurrentYear(userId);
					if (userAlreadyInvested == null) {
					    userAlreadyInvested = BigDecimal.ZERO;
					}
		  
			         BigDecimal maxInvestAmount = new BigDecimal(configCacheService.getConstant("max_invest_amount"));

			         offerMoney.setUserReachedMaxInvest( isUserReacedMax(offerMoney.getNewTotalAfterOffer(),userAlreadyInvested,maxInvestAmount,userId )  );		  
			         offerMoney.setMaxInvestAmount(maxInvestAmount);
			         offerMoney.setUserAlreadyInvested(userAlreadyInvested);
		                   
			
          logger.info("\"****** Calcuate offer money ends ******** ");
		return offerMoney;
			
		
		
	}
	public boolean  isUserReacedMax(BigDecimal  investingAmount,BigDecimal userAlreadyInvested ,BigDecimal maxInvestAmount,int userId)
	{
	       logger.info("entering into isUserReacedMax ");
           // BigDecimal originalAmount = investingAmount; 			
 
			BigDecimal newInvestedAmount = userAlreadyInvested.add(investingAmount);

			
			
			
			
			logger.info("Invested Report : already user "+ userId  +" already  invested "
					+ userAlreadyInvested  + " current share amount " + newInvestedAmount
					+ ":  max invest limit : "+ maxInvestAmount  );
			
		
				if (userAlreadyInvested.compareTo(maxInvestAmount) > 0) {
				   // throw new RuntimeException("User investing amount " + userAlreadyInvested 
				     //   + " cannot be greater than " + maxInvestAmount);
					  return true;
					
				}
				
				
				
			return false;	

		
	}
	
	
	
	
	public BigDecimal  calculatePriceAfterOfferDiscount( BigDecimal originalAmount,BigDecimal totalDiscountAmount)
	{
		
		BigDecimal amountInvested = originalAmount;
		
		
	    if(totalDiscountAmount.compareTo(BigDecimal.valueOf(0) )> 0)
		{
		logger.info("Offer found so amount should be reduced for discuount amount");
		logger.info("original amount " + amountInvested  + " discount " +totalDiscountAmount);
		amountInvested = amountInvested.subtract(totalDiscountAmount);
		logger.info("after discoun original amount " + amountInvested );
        }
	    
	    
	    return amountInvested;

		
	}
	
	
	public BuyGetMoneyResult calculateOfferPriceWithTotalShares(
	        BigDecimal pricePerShare,
	        int paidShareCount,  // how many shares user is willing to pay for
	        int buyQty,
	        int getQty
	) {
	    

	    // How many full offers apply
	    int fullBundles = paidShareCount / buyQty;

	    int freeShares = fullBundles * getQty;
	    int totalShares = paidShareCount + freeShares;

	    BigDecimal totalPayableAmount = pricePerShare.multiply(BigDecimal.valueOf(paidShareCount));

	    return BuyGetMoneyResult.builder().totalPayableAmount(totalPayableAmount)
	    		.totalSharesWithOffer(totalShares).freeShare(freeShares).build();
	}
	
}
