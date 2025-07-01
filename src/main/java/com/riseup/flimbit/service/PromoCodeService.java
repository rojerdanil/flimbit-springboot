package com.riseup.flimbit.service;


import java.util.List;

import com.riseup.flimbit.entity.PromoCode;

public interface PromoCodeService {
    PromoCode save(PromoCode promoCode);
    List<PromoCode> getAll();
    PromoCode getById(Long id);
    PromoCode getByCode(String code);
    void delete(Long id);
}
