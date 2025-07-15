package com.riseup.flimbit.serviceImp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.MovieInvestment;
import com.riseup.flimbit.entity.InvestmentStatus;
import com.riseup.flimbit.entity.Movie;
import com.riseup.flimbit.entity.User;
import com.riseup.flimbit.entity.dto.UserInvestmentSectionDTO;
import com.riseup.flimbit.entity.dto.UserInvestmentSharTypeDTO;
import com.riseup.flimbit.repository.MovieInvestRepository;
import com.riseup.flimbit.repository.MoviesRepository;
import com.riseup.flimbit.repository.UserRepository;
import com.riseup.flimbit.request.MovieIinvestSuccess;
import com.riseup.flimbit.request.MovieInvestRequest;
import com.riseup.flimbit.request.MovieRequest;
import com.riseup.flimbit.request.PayAllShareReturnRequest;
import com.riseup.flimbit.request.StatusRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.SuccessResponse;
import com.riseup.flimbit.service.MovieInvestService;

import jakarta.transaction.Transactional;

@Service
public class MovieInvestServiceImp implements MovieInvestService {
	Logger logger
    = LoggerFactory.getLogger(MovieInvestServiceImp.class);
	@Autowired
	MoviesRepository movieRepository;
	
	@Autowired
	MovieInvestRepository movieInvestRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Override
	public CommonResponse getBuyShares(MovieInvestRequest movieInvRequest,String phoneNumber) {
		// TODO Auto-generated method stub
		Optional<Movie> movieOpt = movieRepository.findById(Long.valueOf(movieInvRequest.getMovieId()));
		if(movieOpt.isPresent())
		{
			Movie movie = movieOpt.get();
			int originalAmount = movie.getBudget();
			int perShareAmount = movie.getPerShareAmount();
			int investedAmount = movieInvestRepo.getInvestedAmountByMovieId(movieInvRequest.getMovieId());			
			int newInvestedAmoutUser = investedAmount + (perShareAmount * movieInvRequest.getNumberOfShares());
			
			logger.info(" invested amount "+  investedAmount);
			if(newInvestedAmoutUser > originalAmount)
			{
				logger.info("user can not buy share by userId " +  phoneNumber );

				int remainAmount = originalAmount - investedAmount;
				 int buyableShares = 0;
				if(remainAmount >=  perShareAmount)
					buyableShares = remainAmount / perShareAmount;
				return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(" Buyable share :" + buyableShares +" Buyable Amount :" + remainAmount ).build();

			}
			else
			{
				logger.info("user can buy share by userId " +  phoneNumber );
				Optional<User> optUser = userRepo.findByphoneNumber( phoneNumber);
				
				if(optUser.isPresent())
				{
					User user = optUser.get();
					if(user.getStatus().equalsIgnoreCase(Messages.STATUS_ACTIVE) )
						return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.USER_NOT_ACTIVE).build();
					else
					{
						logger.info("user buying share "+ user.getPhoneNumber() + " perShareAmount " + perShareAmount );

						int userInvest = perShareAmount * movieInvRequest.getNumberOfShares();
						
						logger.info("amount " + userInvest + " shares" +  movieInvRequest.getNumberOfShares());
						Optional<MovieInvestment> movieInvesOpt = movieInvestRepo.findByUserIdAndMovieIdAndShareTypeId(user.getId(),movieInvRequest.getMovieId(),movieInvRequest.getShareTypeId()); 
						
						if(movieInvesOpt.isPresent())
						{
							logger.info("user buying share have already buyed  "+ user.getPhoneNumber() + " : " +movieInvRequest.getMovieId());
                        
							MovieInvestment movieInves = movieInvesOpt.get();
							movieInves.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
							MovieIinvestSuccess invesSucces = MovieIinvestSuccess.builder()
									.movieId(movieInvRequest.getMovieId())
									.numberOfNewShares(movieInvRequest.getNumberOfShares())
									.perShareAmount(movie.getPerShareAmount())
									.numberOfOldShares(movieInves.getNumberOfShares())
									.oldShareAmount(movieInves.getAmountInvested())
									.build();
							movieInves.setNumberOfShares(movieInves.getNumberOfShares() + movieInvRequest.getNumberOfShares() );
                            BigDecimal amountInvested = movieInves.getAmountInvested().add(BigDecimal.valueOf(userInvest));
							movieInves.setAmountInvested(amountInvested);

							movieInvestRepo.save(movieInves);

							return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message("New and Old Share merged successfully" ).result(invesSucces).build();
						}
						else
						{	
					    logger.info("user buying share newly  "+ user.getPhoneNumber() + " : " +movieInvRequest.getMovieId());
						MovieInvestment movieInves = new MovieInvestment();
						movieInves.setAmountInvested(BigDecimal.valueOf( userInvest));
						movieInves.setMovieId(movieInvRequest.getMovieId());
						movieInves.setNumberOfShares(movieInvRequest.getNumberOfShares());
						movieInves.setUserId(Integer.parseInt(user.getId()+""));
						movieInvestRepo.save(movieInves);
						MovieIinvestSuccess invesSucces = MovieIinvestSuccess.builder()
								.movieId(movieInvRequest.getMovieId())
								.numberOfNewShares(movieInvRequest.getNumberOfShares())
								.perShareAmount(movie.getPerShareAmount())
								.numberOfOldShares(0)
								.oldShareAmount(BigDecimal.valueOf(0)).build();
						return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message("New Share buyed successfully" ).result(invesSucces).build();
				

						}
					}
					
					
				}
				else
					return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.REG_PHONE_NUMBER_NOT_FOUND).build();

 
				
			}
			
			
		}
		else
		{
			return CommonResponse.builder().status(Messages.STATUS_FAILURE).message("Movie is not found by given Id").build();
		}
		
	}

	@Override
	public Page<UserInvestmentSectionDTO> getMovieInvestForUserInvestSection(int language,int movie,String status,String searchText, int start, int length,
			String sortColumn, String sortOrder) {
		// TODO Auto-generated method stub

		    int page = start / length;
	        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
	        Pageable pageable = PageRequest.of(page, length, sort);

	        
	       return  movieInvestRepo.getSearchMovieInvForUserInvestSection(language,movie,status,searchText, pageable);
			/*
			 * System.out.println("total records "+ pageRecords.getTotalElements());
			 * if(pageRecords.getTotalElements() > 0) return pageRecords;
			 */
	       // return movieInvestRepo.findSearchMovieInvForAllUser(pageable);
	}

	@Override
	public List<UserInvestmentSharTypeDTO> readInvestmentWithShareTypeByMovId(int movId,int userId) {
		// TODO Auto-generated method stub
		
	List<UserInvestmentSharTypeDTO> list = movieInvestRepo.getInvestmentsWithShareTypeByMovId(movId,userId)
		        .orElseThrow(() -> new RuntimeException("No investment details for movie " + movId));
		
		return list;		
	}

	@Override
	public MovieInvestment updateInvestmentStatus(StatusRequest statusReq) {
		// TODO Auto-generated method stub
	  MovieInvestment movieInvest = movieInvestRepo.findById(statusReq.getId())
	   .orElseThrow(() -> new RuntimeException("Investment id is not found " + statusReq.getId()));
		
	  if(movieInvest.getStatus().equals(statusReq.getStatus()))
		  throw new RuntimeException("status is same can not be changed");
	  
	  
	  
	  //movieInvest.setStatus(statusReq.getStatus().toLowerCase());
	  movieInvest.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
	  InvestmentStatus invesStatus = new InvestmentStatus();
	  invesStatus.setInvestmentId(statusReq.getId());
	  invesStatus.setDescription(statusReq.getDescription());
	  invesStatus.setStatus(statusReq.getStatus());
	  
	  
	 return  movieInvestRepo.save(movieInvest);
	  
	  
	  
	  
	  
	}

	@Override
	public List<UserInvestmentSharTypeDTO> getInvestmentsForMovIdAndUserIdAndShareTypeId(int movId, int userId,
			int shareId) {
		// TODO Auto-generated method stub
		List<UserInvestmentSharTypeDTO> list = movieInvestRepo.getInvestmentsForMovIdAndUserIdAndShareTypeId(movId,userId,shareId
				)
		        .orElseThrow(() -> new RuntimeException("No investment details for movie " + movId));
		
		return list;
	}

	@Transactional
	@Override
	public SuccessResponse repayShareInvestMoneyToUser(PayAllShareReturnRequest request) {
		// TODO Auto-generated method stub
		
	MovieInvestment investment =	movieInvestRepo.findByUserIdAndMovieIdAndShareTypeId(request.getUserId(),request.getMovieId(),request.getShareTypeId())
        .orElseThrow(() -> new RuntimeException("No investment details for Share type  " + request.getShareTypeId()));
	 
	    BigDecimal repayAmount = BigDecimal.valueOf(request.getAmount());
 
	
	   if(repayAmount.compareTo(investment.getAmountInvested()) == 0)
	   {
		   
		   investment.setAmountInvested(BigDecimal.valueOf(0));
		   investment.setNumberOfShares(0);
		   investment.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
		   investment.setReturnAmount(BigDecimal.valueOf(0));
		   movieInvestRepo.save(investment);
		   
		   
	   }
	
		
		return SuccessResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_SUCCESS).build();
	}

}
