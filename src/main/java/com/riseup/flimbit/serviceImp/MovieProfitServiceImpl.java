	package com.riseup.flimbit.serviceImp;

	import com.riseup.flimbit.constant.ActionType;
import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.constant.MovieProfitPayoutStatus;
import com.riseup.flimbit.constant.PaymentStatus;
import com.riseup.flimbit.constant.SchedularNames;
import com.riseup.flimbit.constant.StatusEnum;
import com.riseup.flimbit.entity.JobHolder;
import com.riseup.flimbit.entity.MoviePayoutStatusHistory;
import com.riseup.flimbit.entity.MovieProfitSummary;
import com.riseup.flimbit.entity.dto.MovieInvestmentSummaryDTO;
import com.riseup.flimbit.entity.dto.MoviePayoutStatusHistoryDTO;
import com.riseup.flimbit.entity.dto.MovieProfitRawDataDTO;
import com.riseup.flimbit.entity.dto.UserPayoutInitateStatusSummaryDTO;
import com.riseup.flimbit.repository.JobHolderRepository;
import com.riseup.flimbit.repository.MovieInvestRepository;
import com.riseup.flimbit.repository.MoviePayoutStatusHistoryRepository;
import com.riseup.flimbit.repository.MovieProfitSummaryRepository;
import com.riseup.flimbit.repository.UserPayoutInitiationRepository;
import com.riseup.flimbit.request.JopRequest;
import com.riseup.flimbit.request.MoviePayoutStatusRequest;
import com.riseup.flimbit.request.MovieProfitRequest;
import com.riseup.flimbit.response.MovieInvestmentShareResposne;
import com.riseup.flimbit.response.MovieInvestmentSummaryResposne;
import com.riseup.flimbit.response.dto.MoviePayoutDTO;
import com.riseup.flimbit.response.dto.MovieProfitSummaryLastHistoryStatus;
import com.riseup.flimbit.security.UserContext;
import com.riseup.flimbit.security.UserContextHolder;
import com.riseup.flimbit.service.MovieProfitService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

	import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

	@Service
	public class MovieProfitServiceImpl implements MovieProfitService {
		
		Logger logger = LoggerFactory.getLogger(MovieProfitServiceImpl.class);


		@Autowired
	    private  MovieInvestRepository moviesInvestmentRepository;
		
		@Autowired
		MovieProfitSummaryRepository  movieProfitRepo;
		
		@Autowired
		AuditLogServiceImp aduit;
		
		@Autowired
		MoviePayoutStatusHistoryRepository  movieStatusHistoryRepo;
		
		@Autowired
		UserPayoutInitiationRepository userPayoutInitateRepo;
		
		@Autowired
		JobHolderRepository failedJobRepo;
		


	    @Override
	    public MovieInvestmentShareResposne calculateMovieProfit(MovieProfitRequest request) {
	    	
	   
	    	MovieInvestmentSummaryDTO  movieInvestment =  moviesInvestmentRepository.getMovieInvestmentSummary(request.getMovieId());
	    	if(movieInvestment == null)
	    		throw new RuntimeException("There is no investment for this movie");
	    	
	    	
	   
	    	
	        List<MovieProfitRawDataDTO> rawDataList = moviesInvestmentRepository.findMovieProfitData(request.getMovieId());

		    List<MoviePayoutDTO> listSharePayout = new ArrayList<MoviePayoutDTO>();

	        
	        // ✅ Here you can do further calculation in Java if needed
	        BigDecimal totalReturn = new BigDecimal(request.getTotalReturnAmount());

	        BigDecimal  totalDiscount = BigDecimal.valueOf(0);
            BigDecimal  totalWalletAmount= BigDecimal.valueOf(0);
            int totalFreeShare = 0; 
    	   
    	   if(rawDataList != null && rawDataList.size() > 0)
    	   {
    		   for(MovieProfitRawDataDTO movieData : rawDataList)
    		   {
    			   
                 totalDiscount = totalDiscount.add(movieData.getTotalDiscountAmount())   ;
                 totalWalletAmount = totalWalletAmount.add(movieData.getTotalWalletAmount());
                 totalFreeShare  =  totalFreeShare + movieData.getTotalFreeShare();
                 
  			   MoviePayoutDTO moviePayoutDTO = MoviePayoutDTO.builder()
  					                .platformCommision(movieData.getPlatformCommision())
  					                .platformCommissionAppliedShares(movieData.getPlatformCommissionAppliedShares())
  					                .profitCommision(movieData.getProfitCommision())
  					                .profitCommissionAppliedShares(movieData.getProfitCommissionAppliedShares())
  					                .shareTypeId(movieData.getShareTypeId())
  					                .shareTypeName(movieData.getShareTypeName())
  					                .totalDiscountAmount(movieData.getTotalDiscountAmount())
  					                .totalFreeShare(movieData.getTotalFreeShare())
  					                .totalSharesSold(movieData.getTotalSharesSold())
  					                .totalWalletAmount(movieData.getTotalWalletAmount())
  					                .profitPerShareType(calculateProfitPerShareType(totalReturn
  					                		,movieInvestment.getTotalInvestedAmount(),movieInvestment.getTotalSharesPurchased(),  movieData.getTotalSharesSold()
  					                		))
  					                .perShareProfit(calculatePerShareProfit(totalReturn
  					                		,movieInvestment.getTotalInvestedAmount(),movieInvestment.getTotalSharesPurchased()
  					                		))
  					                .build();
  			   
  			 BigDecimal eligibleShareForPlatfComm =
  				    BigDecimal.valueOf(moviePayoutDTO.getTotalSharesSold() - moviePayoutDTO.getPlatformCommissionAppliedShares())
  				    .multiply(movieInvestment.getPerShareAmount());
  			
  			BigDecimal eligibleShareForProfitComm =
  				    BigDecimal.valueOf(moviePayoutDTO.getTotalSharesSold() - moviePayoutDTO.getProfitCommissionAppliedShares())
  				    .multiply(moviePayoutDTO.getPerShareProfit());
  			
  			 
  			 moviePayoutDTO.setTotalplatFormCommission(calculateCommission(eligibleShareForPlatfComm
  					 ,BigDecimal.valueOf( moviePayoutDTO.getPlatformCommision())));
  			 
  			moviePayoutDTO.setTotalprofitCommission(calculateCommission(eligibleShareForProfitComm
  					 ,BigDecimal.valueOf( moviePayoutDTO.getProfitCommision())));
  			
  			BigDecimal investedAmount =movieInvestment.getPerShareAmount().multiply(BigDecimal.valueOf( moviePayoutDTO.getTotalSharesSold()));
  			
  			moviePayoutDTO.setNetPay(moviePayoutDTO.getProfitPerShareType().subtract(moviePayoutDTO.getTotalprofitCommission())
  					.add(investedAmount));  					   

  			 listSharePayout.add(moviePayoutDTO);
  			 

    		   }
    	   }	
    	
	        
    	          
    	   MovieInvestmentShareResposne shareResponse = MovieInvestmentShareResposne.builder()
    			     .movieId(movieInvestment.getMovieId())
	    			.movieName(movieInvestment.getMovieName())
	    			.totalInvestedAmount(movieInvestment.getTotalInvestedAmount())
	    			.totalSharesPurchased(movieInvestment.getTotalSharesPurchased())
	    			.totalInvestors(movieInvestment.getTotalInvestors())
	    			.budget(movieInvestment.getBudget())
	    			.perShareAmount(movieInvestment.getPerShareAmount())
	    			.totalDiscount(totalDiscount)
	    			.totalWalletAmount(totalWalletAmount)
	    			.totalFreeShare(totalFreeShare)
	    			.listSharePayout(listSharePayout)
	    			.build();

	        

	        return shareResponse;
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

		@Override
		public MovieInvestmentSummaryResposne readMovieInfoById(int id) {
			// TODO Auto-generated method stub
	    	MovieInvestmentSummaryDTO  movieInvestment =  moviesInvestmentRepository.getMovieInvestmentSummary(id);
	    	if(movieInvestment == null)
	    		throw new RuntimeException("There is no investment for this movie");
	     
	    	
	    	   List<MovieProfitRawDataDTO> rawDataList = moviesInvestmentRepository.findMovieProfitData(id);

	    	    BigDecimal  totalDiscount = BigDecimal.valueOf(0);
	            BigDecimal  totalWalletAmount= BigDecimal.valueOf(0);
	            int totalFreeShare = 0; 
	    	   
	    	   if(rawDataList != null && rawDataList.size() > 0)
	    	   {
	    		   for(MovieProfitRawDataDTO movieData : rawDataList)
	    		   {
	                 totalDiscount = totalDiscount.add(movieData.getTotalDiscountAmount())   ;
	                 totalWalletAmount = totalWalletAmount.add(movieData.getTotalWalletAmount());
	                 totalFreeShare  =  totalFreeShare + movieData.getTotalFreeShare();
	    		   }
	    	   }
	    	   
	    	
	    	 return  MovieInvestmentSummaryResposne
	    			.builder().movieId(movieInvestment.getMovieId())
	    			.movieName(movieInvestment.getMovieName())
	    			.totalInvestedAmount(movieInvestment.getTotalInvestedAmount())
	    			.totalSharesPurchased(movieInvestment.getTotalSharesPurchased())
	    			.totalInvestors(movieInvestment.getTotalInvestors())
	    			.budget(movieInvestment.getBudget())
	    			.perShareAmount(movieInvestment.getPerShareAmount())
	    			.totalDiscount(totalDiscount)
	    			.totalWalletAmount(totalWalletAmount)
	    			.totalFreeShare(totalFreeShare)
	    			.build();

		

		}

		@Override
		public MovieProfitSummaryLastHistoryStatus getMovieProfitDistributeWithLastHistory(int movieId) {
			// TODO Auto-generated method stub
			
			MovieProfitSummary movieProftSummary = movieProfitRepo.findByMovieId(movieId);
			if(movieProftSummary == null)
				return null;
			
			
			
			if(movieProftSummary.getPaymentStatus() != null)
			{
				if( movieProftSummary.getStatus() .equalsIgnoreCase(MovieProfitPayoutStatus.PAYMENT_COMPLETED.name())
					|| movieProftSummary.getStatus().equalsIgnoreCase(MovieProfitPayoutStatus.PAYMENT_FULLY_FAILED.name())	
					|| movieProftSummary.getStatus().equalsIgnoreCase(MovieProfitPayoutStatus.PAYMENT_PARTIALLY_COMPLETED.name())
				)
				{
					movieProftSummary.setPaymentStatus(movieProftSummary.getStatus());

					movieProftSummary.setStatus(MovieProfitPayoutStatus.COMPLETED.name().toLowerCase());	
					
				}
					
			}
			
			
			
			MoviePayoutStatusHistoryDTO moviePayotStatusDto = movieStatusHistoryRepo.findLatestHistoryByMovieProfitId(movieProftSummary.getId());
			
			UserPayoutInitateStatusSummaryDTO userPayoutInit =  userPayoutInitateRepo.countUserPayoutStatusSummary(movieId);
			
			MovieProfitSummaryLastHistoryStatus  movieHistoryStaus = MovieProfitSummaryLastHistoryStatus
					 .builder().movieProfitSummary(movieProftSummary).lastHistory(moviePayotStatusDto)
					 .userPayoutIniateSummary(userPayoutInit)
					 .build();			
			return movieHistoryStaus;
		}

		@Transactional
		@Override
		public MovieProfitSummary getInitiateMoviePayout(MovieProfitRequest request) {
			// TODO Auto-generated method stub
			
			UserContext  loginUser = UserContextHolder.getContext();
			
		   logger.info("**** Enter into getInitate Payement *****");	
			MovieProfitSummary  movieProfit = movieProfitRepo.findByMovieId(request.getMovieId());
			if(movieProfit != null)
				throw new RuntimeException(" Movie is already initiated");
			
			aduit.logAction(0, ActionType.CREATE.name(),
 					EntityName.MOVIE_PAYOUT.name(),loginUser.getUserId(),
 					"Movie Payment distribution is started ",
 					request);
			
			
			MovieProfitSummary  movieProfitNew = new MovieProfitSummary();
	        BigDecimal totalReturn = new BigDecimal(request.getTotalReturnAmount());

			
			movieProfitNew.setMovieId(request.getMovieId());
			movieProfitNew.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			movieProfitNew.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			movieProfitNew.setRemarks("movie starts initiated");
			movieProfitNew.setStatus(MovieProfitPayoutStatus.INITIATED.name());
			movieProfitNew.setTotalProfit(totalReturn);
			movieProfitNew = movieProfitRepo.save(movieProfitNew);
			
			MoviePayoutStatusHistory movieStatusHistory = new MoviePayoutStatusHistory();
			movieStatusHistory.setApproverId(loginUser.getUserId());
			movieStatusHistory.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			movieStatusHistory.setMovieProfitSummaryId(movieProfitNew.getId());
			movieStatusHistory.setReason("movie starts initiated");
			movieStatusHistory.setStatus(MovieProfitPayoutStatus.INITIATED.name());
			movieStatusHistoryRepo.save(movieStatusHistory);			
			
			return movieProfitNew;
		}

		@Override
		public List<MoviePayoutStatusHistoryDTO> getMoviePayoutHistory(int id) {
			// TODO Auto-generated method stub
			return movieStatusHistoryRepo.findHistoryByMovieProfitId(id);
		}
        @Transactional
		@Override
		public MoviePayoutStatusHistory updateStatus(MoviePayoutStatusRequest moviePayoutRequest) {
			// TODO Auto-generated method stub
			
			UserContext  loginUser = UserContextHolder.getContext();
		    MovieProfitSummary movieProfit =	movieProfitRepo.findById(moviePayoutRequest.getMovPayoutId())
				    .orElseThrow(() -> new RuntimeException(" Movie profit distripution is not available"));

			MoviePayoutStatusHistory movieStatusHistory = new MoviePayoutStatusHistory();
			movieStatusHistory.setApproverId(loginUser.getUserId());
			movieStatusHistory.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			movieStatusHistory.setMovieProfitSummaryId(movieProfit.getId());
			movieStatusHistory.setReason(moviePayoutRequest.getComment());
			movieStatusHistory.setStatus(moviePayoutRequest.getStatus());
			
			aduit.logAction(0, ActionType.UPDATE.name(),
 					EntityName.MOVIE_PAYOUT.name(),loginUser.getUserId(),
 					"Movie Payment status change",
 					movieStatusHistory);
			
			movieProfit.setStatus(moviePayoutRequest.getStatus());	
			movieProfit.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			movieProfitRepo.save(movieProfit);
			movieStatusHistory =	movieStatusHistoryRepo.save(movieStatusHistory);	
           
			if(moviePayoutRequest.getStatus().equalsIgnoreCase(MovieProfitPayoutStatus.APPROVED.name())  
					&& movieProfit.getPaymentStatus().equalsIgnoreCase(MovieProfitPayoutStatus.READY_FOR_PAYMENT.name())
					)
			{
				JobHolder  jobHolder = JobHolder.builder()
						.movieId(movieProfit.getMovieId())
						.status(StatusEnum.ACTIVE.name().toLowerCase())
						.createdDate(new Timestamp(System.currentTimeMillis()))
						.jobName(SchedularNames.PAYMENT_APPROVED.name().toLowerCase())
						.build();
				failedJobRepo.save(jobHolder);
			}
			

			return movieStatusHistory;
		}

        @Transactional
		@Override
		public String createNewJobHolder(JopRequest jobrequest) {
			// TODO Auto-generated method stub
			 JobHolder failedJobAlreadyAvailable = failedJobRepo.findByMovieIdAndJobNameIgnoreCase
					 (jobrequest.getMovieId() ,jobrequest.getJobName());
			 MovieProfitSummary movieProfit =	movieProfitRepo.findByMovieId(jobrequest.getMovieId());
			 if(movieProfit == null)
					    new RuntimeException(" Movie profit distripution is not available");
			 
			 
			if(failedJobAlreadyAvailable != null)
			{
				System.out.println(" sdf" +failedJobAlreadyAvailable.getStatus());
				
				movieProfit.setStatus(MovieProfitPayoutStatus.APPROVED.name().toLowerCase());
				movieProfit.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				movieProfitRepo.save(movieProfit);
				if(failedJobAlreadyAvailable.getStatus().equalsIgnoreCase(StatusEnum.ACTIVE.name()))
					return " Job is alread active and will be executed based on schedular";
				else
				{
					failedJobAlreadyAvailable.setCreatedDate(new Timestamp(System.currentTimeMillis()));
					failedJobAlreadyAvailable.setStatus(StatusEnum.ACTIVE.name().toLowerCase());
					
					 failedJobRepo.save(failedJobAlreadyAvailable);
					 return "Job status is changed";
				}
			}
			else
			{
			JobHolder  failedJob = JobHolder.builder()
					.movieId(jobrequest.getMovieId())
					.status(StatusEnum.ACTIVE.name().toLowerCase())
					.createdDate(new Timestamp(System.currentTimeMillis()))
					.jobName(jobrequest.getJobName().toLowerCase())
					.build();
			 return "New job is created";
			
         }
			
		}
			
		
	}
