package com.riseup.flimbit.service;


import java.util.List;

import com.riseup.flimbit.entity.UserOffer;
import com.riseup.flimbit.entity.dto.UserOfferDto;

public interface UserOfferService {
    UserOffer save(UserOffer userOffer);
    List<UserOffer> getAll();
    UserOffer getById(int id);
    void delete(int id);
    List<UserOfferDto>  getUserOfferByUserId(int id) ;
}
