package com.riseup.flimbit.service;

import java.util.List;

import com.riseup.flimbit.request.MovieRequest;
import com.riseup.flimbit.request.MovieSearchRequest;
import com.riseup.flimbit.response.CommonResponse;

public interface MovieService {
	
	public CommonResponse addMovie(MovieRequest movieRequest);
	public  CommonResponse updateMovie(MovieRequest movieRequest);
	public  CommonResponse deleteMovie(List<Integer> ids);
	public  CommonResponse getMoviesByLanguage(MovieSearchRequest movieSearchRequest);


}
