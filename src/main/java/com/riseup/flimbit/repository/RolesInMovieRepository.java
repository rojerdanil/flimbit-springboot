package com.riseup.flimbit.repository;

import com.riseup.flimbit.entity.RolesInMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesInMovieRepository extends JpaRepository<RolesInMovie, Integer> {
}
