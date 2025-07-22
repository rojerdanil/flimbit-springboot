package com.riseup.flimbit.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;

import com.riseup.flimbit.entity.MoviePerson;
import com.riseup.flimbit.entity.dto.ActorDTO;
import com.riseup.flimbit.request.MoviePersonRequest;
import com.riseup.flimbit.response.CommonResponse;

public interface MoviePersonService {
	
	MoviePerson create(MoviePersonRequest person);
    MoviePerson update(Integer id, MoviePersonRequest person);
    void delete(Integer id);
    List<MoviePerson> findAll();
    List<MoviePerson> findAllByLanguagId(int id);
	Page<ActorDTO> getfillerActorWithLanguage(int language,String searchText, int start, int length,
			String sortColumn, String sortOrder);
	MoviePerson findByActorId(Integer Id);
}
