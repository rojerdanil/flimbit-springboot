package com.riseup.flimbit.service;

import java.util.List;

import com.riseup.flimbit.entity.MovieActor;
import com.riseup.flimbit.request.MovieActorRequest;
import com.riseup.flimbit.response.CommonResponse;

public interface MovieActorService {
	CommonResponse create(MovieActorRequest actor);
    MovieActor update(Integer id, MovieActor actor);
    void delete(Integer id);
    List<MovieActor> findAll();
}
