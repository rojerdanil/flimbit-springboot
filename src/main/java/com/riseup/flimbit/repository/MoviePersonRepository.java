package com.riseup.flimbit.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.riseup.flimbit.entity.MoviePerson;
import com.riseup.flimbit.request.MoviePersonRequest;

public interface MoviePersonRepository extends JpaRepository<MoviePerson, Integer> {
	
	Optional<MoviePerson> findByNameIgnoreCaseAndLanguage(String name,int language);
	List<MoviePerson>  findByLanguage(int language);
}
