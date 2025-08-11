package com.riseup.flimbit.service;

import java.util.List;

import com.riseup.flimbit.entity.OfferShareTypeMovie;
import com.riseup.flimbit.request.MovieShareOfferRequest;

public interface MovieShareOfferMapService {
	 OfferShareTypeMovie save(MovieShareOfferRequest offerShareTypeMovie);
	 
	 OfferShareTypeMovie update(long id,MovieShareOfferRequest offerShareTypeMovie);

	    List<OfferShareTypeMovie> getAll();
	    OfferShareTypeMovie getById(Long id);
	    void delete(Long id);
}
