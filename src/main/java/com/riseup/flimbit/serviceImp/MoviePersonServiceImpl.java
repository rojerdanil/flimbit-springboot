package com.riseup.flimbit.serviceImp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.ActionType;
import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.MoviePerson;
import com.riseup.flimbit.entity.dto.ActorDTO;
import com.riseup.flimbit.repository.MoviePersonRepository;
import com.riseup.flimbit.request.MoviePersonRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.security.UserContextHolder;
import com.riseup.flimbit.service.MoviePersonService;
import com.riseup.flimbit.utility.CommonUtilty;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MoviePersonServiceImpl implements MoviePersonService {

    @Autowired
    private MoviePersonRepository movPersRepository;
    
    @Autowired
    AuditLogServiceImp audit;

    @Transactional
    @Override
    public MoviePerson create(MoviePersonRequest personRequest) {
    	
      // name and language is mandatory
    	
    	Optional<MoviePerson>  moviePerOpt =movPersRepository.findByNameIgnoreCaseAndLanguage(personRequest.getName(),personRequest.getLanguage());
    	if(moviePerOpt.isPresent())
    	{
    		 throw new RuntimeException("Same name is available for language ." );
    	}
    	
    	
    	MoviePerson moviePerson = movPersRepository.save(CommonUtilty.mapMovPersonReqToMoviePerson(personRequest,new MoviePerson()));
    	 audit.logAction(UserContextHolder.getContext().getUserId(),ActionType.CREATE.name()
  				, EntityName.ACTOR.name(), moviePerson.getId(), "actor is added "   , personRequest);

    	
      return moviePerson;

    }
    @Transactional
    @Override
    public MoviePerson update(Integer id, MoviePersonRequest person) {
       // person.setId(id);
    	MoviePerson moviePerson =movPersRepository.findById(id)
    	.orElseThrow(() -> new RuntimeException("Actor is not found for given id " + id));
  
     	 audit.logAction(UserContextHolder.getContext().getUserId(),ActionType.UPDATE.name()
   				, EntityName.ACTOR.name(), moviePerson.getId(), "actor is updated "   , moviePerson);

    	
        return movPersRepository.save(CommonUtilty.mapMovPersonReqToMoviePerson(person,moviePerson));
    }
    @Transactional
    @Override
    public void delete(Integer id) {
    	MoviePerson moviePerson =movPersRepository.findById(id)
    	    	.orElseThrow(() -> new RuntimeException("Actor is not found for given id " + id));
    	  
    	 audit.logAction(UserContextHolder.getContext().getUserId(),ActionType.DELETE.name()
    				, EntityName.ACTOR.name(), moviePerson.getId(), "actor is deleted "   , moviePerson);

     
    	movPersRepository.deleteById(id);
    }

    @Override
    public List<MoviePerson> findAll() {
        return movPersRepository.findAll();
    }

	@Override
	public List<MoviePerson> findAllByLanguagId(int id) {
		// TODO Auto-generated method stub
		return movPersRepository.findByLanguage(id);
	}

	@Override
	public Page<ActorDTO> getfillerActorWithLanguage(int language, String searchText, int start, int length,
			String sortColumn, String sortOrder) {
		// TODO Auto-generated method stub
		
		 int page = start / length;
	        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
	        Pageable pageable = PageRequest.of(page, length, sort);
		return movPersRepository.getfillerActorWithLanguage(language, searchText, pageable);
	}

	@Override
	public MoviePerson findByActorId(Integer id) {
		// TODO Auto-generated method stub
		return movPersRepository.findById(id)
		    	.orElseThrow(() -> new RuntimeException("Actor is not found for given id " + id));
	}
}
