package com.riseup.flimbit.service;


import java.util.List;

import com.riseup.flimbit.entity.UserOffer;

public interface UserOfferService {
    UserOffer save(UserOffer userOffer);
    List<UserOffer> getAll();
    UserOffer getById(Long id);
    void delete(Long id);
}
