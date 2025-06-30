package com.riseup.flimbit.service;

import java.util.List;

import com.riseup.flimbit.entity.MoviePerson;
import com.riseup.flimbit.request.MoviePersonRequest;
import com.riseup.flimbit.response.CommonResponse;

public interface MoviePersonService {
	
	CommonResponse create(MoviePersonRequest person);
    MoviePerson update(Integer id, MoviePerson person);
    void delete(Integer id);
    List<MoviePerson> findAll();
    List<MoviePerson> findAllByLanguagId(int id);
}
