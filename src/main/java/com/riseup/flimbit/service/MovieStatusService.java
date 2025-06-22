package com.riseup.flimbit.service;



import java.util.List;

import com.riseup.flimbit.entity.MovieStatus;

public interface MovieStatusService {
    MovieStatus create(MovieStatus status);
    MovieStatus update(Integer id, MovieStatus status);
    void delete(Integer id);
    List<MovieStatus> findAll();
}
