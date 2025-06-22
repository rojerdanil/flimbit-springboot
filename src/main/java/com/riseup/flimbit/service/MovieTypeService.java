package com.riseup.flimbit.service;



import java.util.List;

import com.riseup.flimbit.entity.MovieType;

public interface MovieTypeService {
    MovieType addMovieType(MovieType movieType);
    MovieType updateMovieType(MovieType movieType);
    void deleteMovieType(Integer id);
    MovieType getMovieTypeById(Integer id);
    List<MovieType> getAllMovieTypes();
}
