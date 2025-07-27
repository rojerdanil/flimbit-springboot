	package com.riseup.flimbit.serviceImp;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.UserPayoutInitiation;
import com.riseup.flimbit.repository.UserPayoutInitiationRepository;
import com.riseup.flimbit.service.UserPayoutInitiationService;

@Service
	public class UserPayoutInitiationServiceImpl implements UserPayoutInitiationService {

	    @Autowired
	    private UserPayoutInitiationRepository repository;

	    @Override
	    public List<UserPayoutInitiation> findAllFiltered(Integer movieId, Integer shareTypeId) {
	        List<UserPayoutInitiation> all = repository.findAll();
	        return all.stream()
	                .filter(p -> (movieId == null || p.getMovieId() == movieId))
	                .filter(p -> (shareTypeId == null || p.getShareTypeId() == shareTypeId))
	                .collect(Collectors.toList());
	    }
	}