package com.riseup.flimbit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.entity.PromoShareTypeMovie;
import com.riseup.flimbit.service.PromoShareTypeMovieService;

import java.util.List;

@RestController
@RequestMapping("/api/promo-mappings")
public class PromoShareTypeMovieController {

    @Autowired
    private PromoShareTypeMovieService service;

    @PostMapping
    public PromoShareTypeMovie create(@RequestBody PromoShareTypeMovie promoShareTypeMovie) {
        return service.save(promoShareTypeMovie);
    }

    @GetMapping
    public List<PromoShareTypeMovie> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public PromoShareTypeMovie getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
