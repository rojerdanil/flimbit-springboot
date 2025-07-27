package com.riseup.flimbit.service;

import java.util.List;

import com.riseup.flimbit.entity.UserPayoutInitiation;

public interface UserPayoutInitiationService {
    List<UserPayoutInitiation> findAllFiltered(Integer movieId, Integer shareTypeId);


}
