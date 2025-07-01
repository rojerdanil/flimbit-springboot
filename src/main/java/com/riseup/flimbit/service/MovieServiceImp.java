package com.riseup.flimbit.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.constant.MovieStatusEnum;
import com.riseup.flimbit.entity.InvestmentSummary;
import com.riseup.flimbit.entity.Movie;
import com.riseup.flimbit.entity.MovieShareSummaryInterface;
import com.riseup.flimbit.entity.MovieStatus;
import com.riseup.flimbit.repository.MovieInvestRepository;
import com.riseup.flimbit.repository.MovieStatusRepository;
import com.riseup.flimbit.repository.MoviesRepository;
import com.riseup.flimbit.request.DataTableRequest;
import com.riseup.flimbit.request.MovieRequest;
import com.riseup.flimbit.request.MovieSearchRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.MovieResponse;
import com.riseup.flimbit.response.MovieDTOSummary;
import com.riseup.flimbit.utility.CommonUtilty;

import jakarta.transaction.Transactional;

@Service
public class MovieServiceImp implements MovieService{
	
	Logger logger
    = LoggerFactory.getLogger(MovieServiceImp.class);

	@Autowired
	MoviesRepository movieRepository;
	
	@Autowired
	MovieStatusRepository movieStatusRepo;
	
	@Autowired
	MovieInvestRepository InvestmentRepository;
	
	
	@Override
	public CommonResponse addMovie(MovieRequest movieRequest) {
		// TODO Auto-generated method stub
		if(Optional.ofNullable(movieRequest).isPresent())
		{
			Movie movieNew = null;
			if(movieRequest.isEdit() == true)
			{
				Long id = movieRequest.getId();
				Optional<Movie> movieExitOpt = movieRepository.findById(id);
				if(movieExitOpt.isPresent())
				{
					Movie movieExit = movieExitOpt.get();
					movieExit = CommonUtilty.mapToMovieEnity(movieRequest,movieExit);
					movieExit.setUpdatedDate( new Timestamp(System.currentTimeMillis()));
					movieRepository.save(movieExit);

					return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_UPATE_SUCCESS).build();

				}
				else
					return CommonResponse.builder().status(Messages.STATUS_FAILURE).message("movie is not found by id").build();
	
				
			}
			else
			{

				Movie movie = new Movie();
				movie = CommonUtilty.mapToMovieEnity(movieRequest,movie);
				if(Optional.ofNullable(movie).isPresent())
				{
					movieRepository.save(movie);
					return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_UPATE_SUCCESS).build();

				}

				
			}
			
			
			return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.REQUEST_OBJECT_NOTCONVERTED).build();


		}
		else
		{
			return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.REQUEST_OBJECT_EMPTY).build();
		}
	}

	@Override
	public CommonResponse updateMovie(MovieRequest movieRequest) {
		// TODO Auto-generated method stub
		if(Optional.ofNullable(movieRequest).isPresent())
		{
			Optional<Movie> movieExitOpt = movieRepository.findById(movieRequest.getId());
			
			if(movieExitOpt.isPresent())
			{
			Movie movieExit = movieExitOpt.get();

			Movie movie = CommonUtilty.mapToMovieEnity(movieRequest,movieExit);
			if(Optional.ofNullable(movie).isPresent())
			{
				movie.setId(movieRequest.getId());
				movieRepository.save(movie);
				return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_UPATE_SUCCESS).build();

			}
			}
			
			return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.REQUEST_OBJECT_NOTCONVERTED).build();

		}
		else
		{
			return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.REQUEST_OBJECT_EMPTY).build();
		}
	}

	@Override
	@Transactional
	public CommonResponse deleteMovie(List<Integer> ids) {
		// TODO Auto-generated method stub
		if(ids !=null && !ids.isEmpty())
		{
			movieRepository.deleteByIdIn(ids);
			return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_DELETE_SUCCESS).build();

		}
		
		return CommonResponse.builder().status(Messages.STATUS_FAILURE).message("Ids can be empty").build();
	}

	@Override
	public CommonResponse getMoviesByLanguage(MovieSearchRequest movieSearchRequest) {
		// TODO Auto-generated method stub
		/*
		 * List<String> movieStatusList = Arrays.asList(
		 * MovieStatusEnum.IDEA_STAGE.getDisplayName(),
		 * MovieStatusEnum.PRE_PRODUCTION.getDisplayName(),
		 * MovieStatusEnum.FUNDING_OPEN.getDisplayName() );
		 */
		//List<MovieStatus> movieStatusListDb =  movieStatusRepo.findAll();
		List<MovieResponse> movieResList =CommonUtilty.mapToMovieRespFromMoiveEntity(
				movieRepository.findByLanguageIgnoreCaseOrderByCreatedDate(movieSearchRequest.getLanguage(),movieSearchRequest.getLimit(),movieSearchRequest.getOffset(),"true"));
		
			
		return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_SEARCH_SUCCESS).result(	
				movieResList).build();
	}

	@Override
	public CommonResponse getMoviesForDataTable(DataTableRequest request) {
		// TODO Auto-generated method stub
        System.out.println("language " +request.getLanguage() + " objDraw " + request.getDraw());

		String keyword = request.getSearch() != null ? request.getSearch().getValue() : "";
		String language = request.getLanguage();
		if(language != null)
			language = request.getLanguage().isEmpty()  ? "" :request.getLanguage();
		
	    int limit = 10;
	    int offset = request.getStart();
        System.out.println("keyword " +keyword  +" :limit:"+ limit + ":offset:" + offset);
	    List<Movie> movieList = movieRepository.findMoviesWithSearch(keyword, limit, offset,language);
	    List<MovieDTOSummary> dtoList = new ArrayList<>();
	    List<InvestmentSummary> investmentSummaryList = null;
	    if(movieList !=null && movieList.size() > 0)
	    {
	    	List<Long> movieIds = movieList.stream()
	    		    .map(movie -> Long.valueOf(movie.getId()))  // Ensures it's Long
	    		    .collect(Collectors.toList());
	    	investmentSummaryList = InvestmentRepository.getShareSummaryForMovieIds(movieIds);
	    }
	 // Map<movieId, totalSharesSold>
	 Map<Long, Integer> movieToSharesSold = null;
			
	  if(investmentSummaryList != null && investmentSummaryList.size() !=0)
		  movieToSharesSold =investmentSummaryList.stream()
	     .collect(Collectors.toMap(InvestmentSummary::getMovieId, InvestmentSummary::getTotalShares));
	    
	    for (Movie movie : movieList) {
	        MovieDTOSummary dto = new MovieDTOSummary();
	        BeanUtils.copyProperties(movie, dto); // Spring utility
	        int total = movie.getBudget() / movie.getPerShareAmount();
	        int sold = 0;
	        int soldShares = 0;

	        	if(movieToSharesSold  !=null)	 
	        	{
	        		sold = movieToSharesSold.getOrDefault(movie.getId(), 0);
	    	         soldShares = movieToSharesSold.getOrDefault(movie.getId(), 0);

	        	}
	        		

	        String status = "Not Started";
	        if (sold == total) status = "Sold Out";
	        else if (sold > 0) status = "Live";

	        dto.setShareStatus(status);
	        dto.setDaysBeforeRelease(CommonUtilty.getDaysBeforeRelease(movie.getReleaseDate()));

	        int totalShares = movie.getBudget() / movie.getPerShareAmount();
	        int soldAmount = soldShares * movie.getPerShareAmount();
	        int percent = (int) (((double) soldShares / totalShares) * 100);

	        dto.setSharesSold(soldShares);
	        dto.setTotalShares(totalShares);
	        dto.setShareSoldAmount("₹" + soldAmount + " of ₹" + movie.getBudget());
	        dto.setFundingPercent(percent);
	        dto.setActiveStatus(movie.getStatus());
	        dtoList.add(dto);
	    }

	    
	    
	    
	    
	    
	    
	    
	    System.out.println("movielist " + dtoList.size());
	    long total = movieRepository.countMoviesWithSearch(keyword,"");
	    long totalFiltered = dtoList.size(); // total without filter

	    Map<String, Object> response = new HashMap<>();
	    response.put("draw", request.getDraw());
	    response.put("recordsTotal", total);
	    response.put("recordsFiltered", totalFiltered);
	    response.put("data", dtoList);
	    return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_SEARCH_SUCCESS)
	    		.result(response).build();	

}

	@Override
	public CommonResponse findMovieSummaryById(int id) {
		// TODO Auto-generated method stub
		List<String> movieStatusList = Arrays.asList(
				MovieStatusEnum.IDEA_STAGE.getDisplayName(),
				MovieStatusEnum.PRE_PRODUCTION.getDisplayName(),
				MovieStatusEnum.FUNDING_OPEN.getDisplayName()
				);
		//List<MovieStatus> movieStatusListDb =  movieStatusRepo.findAll();
		
		List<MovieShareSummaryInterface> listx = movieRepository.findMovieSummaryById(id);
		List<MovieResponse> movieResList = CommonUtilty.mapToMovieRespFromMoiveEntity(listx);	
		if(movieResList != null && movieResList.size() > 0)
	    	return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_SEARCH_SUCCESS).result(	
				movieResList.get(0)).build();	
		else
			return CommonResponse.builder().status(Messages.STATUS_FAILURE).message("Given movie id is not found "+id).result(	
					movieResList.get(0)).build();	
	}

	@Override
	public CommonResponse findMovieEnityById(int id) {
		// TODO Auto-generated method stub
		long newId = id;
		 Optional<Movie> movieOpt =   movieRepository.findById(newId);
		 if(movieOpt.isPresent())
		 {
			 return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_SUCCESS).result(	
					 CommonUtilty.MapToMovieEntityResponse(movieOpt.get())).build();	
			 
			 
		}
		 return CommonResponse.builder().status(Messages.STATUS_FAILURE).message("Given movie id is not found "+id).build();		}
}
