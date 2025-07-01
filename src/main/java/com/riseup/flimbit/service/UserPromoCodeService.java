package com.riseup.flimbit.service;


import java.util.List;

import com.riseup.flimbit.entity.UserPromoCode;

public interface UserPromoCodeService {
    UserPromoCode save(UserPromoCode userPromoCode);
    List<UserPromoCode> getAll();
    UserPromoCode getById(Long id);
    void delete(Long id);
}

