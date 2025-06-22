package com.riseup.flimbit.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.MovieStatus;

@Repository
public interface MovieStatusRepository extends JpaRepository<MovieStatus, Integer> {
}
