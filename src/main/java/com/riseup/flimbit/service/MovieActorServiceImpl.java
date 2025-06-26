package com.riseup.flimbit.service;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.MovieActor;
import com.riseup.flimbit.repository.MovieActorRepository;
import com.riseup.flimbit.request.MovieActorRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.utility.CommonUtilty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieActorServiceImpl implements MovieActorService {

    @Autowired
    private MovieActorRepository movieActorrepository;

    @Override
    public CommonResponse create(MovieActorRequest movieActorReq) {
    	Optional<MovieActor> movieActorExit  =  movieActorrepository.findByMovieIdAndRoleMovieId(movieActorReq.getMovieId(),movieActorReq.getRoleMovieId());
    	if(movieActorExit.isPresent())
    	{
    		return CommonResponse.builder().status(Messages.STATUS_FAILURE).message("Role has been added alread to this movie").build();
    	}
    	return CommonResponse.builder().status(Messages.STATUS_SUCCESS)
    			.message(Messages.STATUS_UPATE_SUCCESS).result(movieActorrepository.save(CommonUtilty.mapMovieActorReqToMovieActor(movieActorReq))).build();
        
    }

    @Override
    public MovieActor update(Integer id, MovieActor actor) {
        actor.setId(id);
        return movieActorrepository.save(actor);
    }

    @Override
    public void delete(Integer id) {
    	movieActorrepository.deleteById(id);
    }

    @Override
    public List<MovieActor> findAll() {
        return movieActorrepository.findAll();
    }
}
