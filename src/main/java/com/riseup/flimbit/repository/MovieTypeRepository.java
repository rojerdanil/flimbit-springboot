package com.riseup.flimbit.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.MovieType;

@Repository
public interface MovieTypeRepository extends JpaRepository<MovieType, Integer> {
}
