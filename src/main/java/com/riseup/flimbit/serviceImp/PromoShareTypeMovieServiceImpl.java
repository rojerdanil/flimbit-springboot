package com.riseup.flimbit.serviceImp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.PromoShareTypeMovie;
import com.riseup.flimbit.repository.PromoShareTypeMovieRepository;
import com.riseup.flimbit.service.PromoShareTypeMovieService;

import java.util.List;

@Service
public class PromoShareTypeMovieServiceImpl implements PromoShareTypeMovieService {

    @Autowired
    private PromoShareTypeMovieRepository repository;

    @Override
    public PromoShareTypeMovie save(PromoShareTypeMovie promoShareTypeMovie) {
        return repository.save(promoShareTypeMovie);
    }

    @Override
    public List<PromoShareTypeMovie> getAll() {
        return repository.findAll();
    }

    @Override
    public PromoShareTypeMovie getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
