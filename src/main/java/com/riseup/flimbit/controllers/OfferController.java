package com.riseup.flimbit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.entity.Offer;
import com.riseup.flimbit.service.OfferService;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    @Autowired
    private OfferService offerService;

    @PostMapping
    public Offer create(@RequestBody Offer offer) {
        return offerService.save(offer);
    }

    @GetMapping
    public List<Offer> getAll() {
        return offerService.findAll();
    }

    @GetMapping("/{id}")
    public Offer getById(@PathVariable Long id) {
        return offerService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        offerService.delete(id);
    }
}
