package com.riseup.flimbit.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.entity.UserPromoCode;
import com.riseup.flimbit.service.UserPromoCodeService;

import java.util.List;

@RestController
@RequestMapping("/api/user-promo-codes")
public class UserPromoCodeController {

    @Autowired
    private UserPromoCodeService service;

    @PostMapping
    public UserPromoCode create(@RequestBody UserPromoCode userPromoCode) {
        return service.save(userPromoCode);
    }

    @GetMapping
    public List<UserPromoCode> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public UserPromoCode getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
