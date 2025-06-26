package com.riseup.flimbit.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.constant.MovieStatusEnum;
import com.riseup.flimbit.entity.Movie;
import com.riseup.flimbit.entity.MovieShareSummaryInterface;
import com.riseup.flimbit.entity.MovieStatus;
import com.riseup.flimbit.repository.MovieStatusRepository;
import com.riseup.flimbit.repository.MoviesRepository;
import com.riseup.flimbit.request.DataTableRequest;
import com.riseup.flimbit.request.MovieRequest;
import com.riseup.flimbit.request.MovieSearchRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.MovieResponse;
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
	
	
	@Override
	public CommonResponse addMovie(MovieRequest movieRequest) {
		// TODO Auto-generated method stub
		if(Optional.ofNullable(movieRequest).isPresent())
		{
			Movie movieNew = new Movie();
			Movie movie = CommonUtilty.mapToMovieEnity(movieRequest,movieNew);
			if(Optional.ofNullable(movie).isPresent())
			{
				movieRepository.save(movie);
				return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_UPATE_SUCCESS).build();

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
		List<String> movieStatusList = Arrays.asList(
				MovieStatusEnum.IDEA_STAGE.getDisplayName(),
				MovieStatusEnum.PRE_PRODUCTION.getDisplayName(),
				MovieStatusEnum.FUNDING_OPEN.getDisplayName()
				);
		//List<MovieStatus> movieStatusListDb =  movieStatusRepo.findAll();
		List<MovieResponse> movieResList =CommonUtilty.mapToMovieRespFromMoiveEntity(
				movieRepository.findByLanguageIgnoreCaseOrderByCreatedDate(movieSearchRequest.getLanguage(),movieStatusList,movieSearchRequest.getLimit(),movieSearchRequest.getOffset()));
		
			
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
			language = request.getLanguage().isEmpty()  ? null :request.getLanguage();
	    int limit = 10;
	    int offset = request.getStart();
        System.out.println("keyword " +keyword  +" ::"+ limit + offset);
	    List<Movie> movieList = movieRepository.findMoviesWithSearch(keyword, limit, offset,language);
	    System.out.println("movielist " + movieList.size());
	    long total = movieRepository.countMoviesWithSearch(keyword,"");
	    long totalFiltered = movieList.size(); // total without filter

	    Map<String, Object> response = new HashMap<>();
	    response.put("draw", request.getDraw());
	    response.put("recordsTotal", total);
	    response.put("recordsFiltered", totalFiltered);
	    response.put("data", movieList);
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
}
