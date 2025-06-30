package com.riseup.flimbit.repository;

import com.riseup.flimbit.entity.ActorsPlayInMovieInterface;
import com.riseup.flimbit.entity.MovieActor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieActorRepository extends JpaRepository<MovieActor, Integer> {
	
	Optional<MovieActor>  findByMovieIdAndRoleMovieIdAndActorId(int movieId,int roleMovieId,int actorId);
	
	
	@Query(value = " select ms.*,mp.name as actorname,roi.name as rolename from movie_actors as ms "
			+ " join movie_person as mp on ms.movie_actor_id = mp.id"
			+" join roles_in_movie as roi on roi.id = ms.movie_role_id"
	        +" where ms.movie_id = :movieId", nativeQuery = true)
	List<ActorsPlayInMovieInterface>   findByMovieId(@Param("movieId") int id);
	
}
