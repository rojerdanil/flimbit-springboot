package com.riseup.flimbit.service;

import java.sql.Timestamp;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.MovieInvestment;
import com.riseup.flimbit.entity.Movie;
import com.riseup.flimbit.entity.User;
import com.riseup.flimbit.repository.MovieInvestRepository;
import com.riseup.flimbit.repository.MoviesRepository;
import com.riseup.flimbit.repository.UserRepository;
import com.riseup.flimbit.request.MovieIinvestSuccess;
import com.riseup.flimbit.request.MovieInvestRequest;
import com.riseup.flimbit.request.MovieRequest;
import com.riseup.flimbit.response.CommonResponse;

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
						Optional<MovieInvestment> movieInvesOpt = movieInvestRepo.findByUserIdAndMovieId(user.getId(),movieInvRequest.getMovieId()); 
						
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

							movieInves.setAmountInvested(movieInves.getAmountInvested() + userInvest);

							movieInvestRepo.save(movieInves);

							return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message("New and Old Share merged successfully" ).object(invesSucces).build();
						}
						else
						{	
					    logger.info("user buying share newly  "+ user.getPhoneNumber() + " : " +movieInvRequest.getMovieId());
						MovieInvestment movieInves = new MovieInvestment();
						movieInves.setAmountInvested(userInvest);
						movieInves.setMovieId(movieInvRequest.getMovieId());
						movieInves.setNumberOfShares(movieInvRequest.getNumberOfShares());
						movieInves.setUserId(user.getId());
						movieInvestRepo.save(movieInves);
						MovieIinvestSuccess invesSucces = MovieIinvestSuccess.builder()
								.movieId(movieInvRequest.getMovieId())
								.numberOfNewShares(movieInvRequest.getNumberOfShares())
								.perShareAmount(movie.getPerShareAmount())
								.numberOfOldShares(0)
								.oldShareAmount(0).build();
						return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message("New Share buyed successfully" ).object(invesSucces).build();
				

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

}
