package com.riseup.flimbit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.entity.PromoCode;
import com.riseup.flimbit.service.PromoCodeService;

import java.util.List;

@RestController
@RequestMapping("/api/promo-codes")
public class PromoCodeController {

    @Autowired
    private PromoCodeService promoCodeService;

    @PostMapping
    public PromoCode create(@RequestBody PromoCode promoCode) {
        return promoCodeService.save(promoCode);
    }

    @GetMapping
    public List<PromoCode> getAll() {
        return promoCodeService.getAll();
    }

    @GetMapping("/{id}")
    public PromoCode getById(@PathVariable Long id) {
        return promoCodeService.getById(id);
    }

    @GetMapping("/code/{promoCode}")
    public PromoCode getByCode(@PathVariable String promoCode) {
        return promoCodeService.getByCode(promoCode)
        		.orElseThrow(() -> new RuntimeException("Promo Code not found "+ promoCode));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        promoCodeService.delete(id);
    }
}
