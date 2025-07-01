package com.riseup.flimbit.service;

import java.util.List;

import com.riseup.flimbit.entity.OfferShareTypeMovie;

public interface OfferShareTypeMovieService {
	 OfferShareTypeMovie save(OfferShareTypeMovie offerShareTypeMovie);
	    List<OfferShareTypeMovie> getAll();
	    OfferShareTypeMovie getById(Long id);
	    void delete(Long id);
}
