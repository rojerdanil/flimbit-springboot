package com.riseup.flimbit.service;


import java.util.List;

import com.riseup.flimbit.entity.Offer;

public interface OfferService {
    Offer save(Offer offer);
    List<Offer> findAll();
    Offer findById(Long id);
    void delete(Long id);
}
