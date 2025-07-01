package com.riseup.flimbit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.OfferShareTypeMovie;
import com.riseup.flimbit.repository.OfferShareTypeMovieRepository;

import java.util.List;

@Service
public class OfferShareTypeMovieServiceImpl implements OfferShareTypeMovieService {

    @Autowired
    private OfferShareTypeMovieRepository repository;

    @Override
    public OfferShareTypeMovie save(OfferShareTypeMovie offerShareTypeMovie) {
        return repository.save(offerShareTypeMovie);
    }

    @Override
    public List<OfferShareTypeMovie> getAll() {
        return repository.findAll();
    }

    @Override
    public OfferShareTypeMovie getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
