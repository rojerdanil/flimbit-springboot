package com.riseup.flimbit.service;

import com.riseup.flimbit.entity.RolesInMovie;

import java.util.List;

public interface RolesInMovieService {
    RolesInMovie create(RolesInMovie role);
    RolesInMovie update(Integer id, RolesInMovie role);
    void delete(Integer id);
    List<RolesInMovie> findAll();
}
