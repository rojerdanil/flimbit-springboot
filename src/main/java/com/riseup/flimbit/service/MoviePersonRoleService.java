package com.riseup.flimbit.service;

import com.riseup.flimbit.entity.MoviePersonType;

import java.util.List;

public interface MoviePersonRoleService {
    MoviePersonType create(MoviePersonType role);
    MoviePersonType update(Integer id, MoviePersonType role);
    void delete(Integer id);
    List<MoviePersonType> findAll();
}
