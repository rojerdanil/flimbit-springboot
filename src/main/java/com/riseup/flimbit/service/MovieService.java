package com.riseup.flimbit.service;

import java.util.List;

import org.springframework.data.repository.query.Param;

import com.riseup.flimbit.entity.Movie;
import com.riseup.flimbit.entity.MovieShareSummaryInterface;
import com.riseup.flimbit.request.DataTableRequest;
import com.riseup.flimbit.request.MovieRequest;
import com.riseup.flimbit.request.MovieSearchRequest;
import com.riseup.flimbit.response.CommonResponse;

public interface MovieService {
	
	public  CommonResponse addMovie(MovieRequest movieRequest);
	public  CommonResponse updateMovie(MovieRequest movieRequest);
	public  CommonResponse deleteMovie(List<Integer> ids);
	public  CommonResponse getMoviesByLanguage(MovieSearchRequest movieSearchRequest);
	public  CommonResponse  getMoviesForDataTable(DataTableRequest request);
	public  CommonResponse  findMovieSummaryById(int id);
	public  CommonResponse  findMovieEnityById(int id);
	public  List<Movie>  getMovieByLanguage(int id);
	public List<MovieShareSummaryInterface> searchMovie(MovieSearchRequest movieSearchRequest);


	

	


}
