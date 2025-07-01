package com.riseup.flimbit.service;


import java.util.List;

import com.riseup.flimbit.entity.PromoShareTypeMovie;

public interface PromoShareTypeMovieService {
    PromoShareTypeMovie save(PromoShareTypeMovie promoShareTypeMovie);
    List<PromoShareTypeMovie> getAll();
    PromoShareTypeMovie getById(Long id);
    void delete(Long id);
}
