package com.riseup.flimbit.repository;

import com.riseup.flimbit.entity.MovieActor;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieActorRepository extends JpaRepository<MovieActor, Integer> {
	
	Optional<MovieActor>  findByMovieIdAndRoleMovieId(int movieId,int roleMovieId);
}
