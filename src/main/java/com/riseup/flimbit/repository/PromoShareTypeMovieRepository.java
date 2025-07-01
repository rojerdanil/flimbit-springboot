package com.riseup.flimbit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.PromoShareTypeMovie;

@Repository
public interface PromoShareTypeMovieRepository extends JpaRepository<PromoShareTypeMovie, Long> {
}
