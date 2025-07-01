package com.riseup.flimbit.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.entity.UserOffer;
import com.riseup.flimbit.service.UserOfferService;

import java.util.List;

@RestController
@RequestMapping("/api/user-offers")
public class UserOfferController {

    @Autowired
    private UserOfferService service;

    @PostMapping
    public UserOffer create(@RequestBody UserOffer userOffer) {
        return service.save(userOffer);
    }

    @GetMapping
    public List<UserOffer> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public UserOffer getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
