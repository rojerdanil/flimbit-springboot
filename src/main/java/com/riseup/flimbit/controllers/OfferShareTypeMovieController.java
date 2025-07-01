package com.riseup.flimbit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.entity.OfferShareTypeMovie;
import com.riseup.flimbit.service.OfferShareTypeMovieService;

import java.util.List;

@RestController
@RequestMapping("/api/offer-mappings")
public class OfferShareTypeMovieController {

    @Autowired
    private OfferShareTypeMovieService service;

    @PostMapping
    public OfferShareTypeMovie create(@RequestBody OfferShareTypeMovie offerShareTypeMovie) {
        return service.save(offerShareTypeMovie);
    }

    @GetMapping
    public List<OfferShareTypeMovie> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public OfferShareTypeMovie getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
