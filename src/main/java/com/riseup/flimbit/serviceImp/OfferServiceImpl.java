package com.riseup.flimbit.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.Offer;
import com.riseup.flimbit.repository.OfferRepository;
import com.riseup.flimbit.service.OfferService;

import java.util.List;

@Service
public class OfferServiceImpl implements OfferService {

    @Autowired
    private OfferRepository offerRepository;

    @Override
    public Offer save(Offer offer) {
        return offerRepository.save(offer);
    }

    @Override
    public List<Offer> findAll() {
        return offerRepository.findAll();
    }

    @Override
    public Offer findById(Long id) {
        return offerRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        offerRepository.deleteById(id);
    }
}
