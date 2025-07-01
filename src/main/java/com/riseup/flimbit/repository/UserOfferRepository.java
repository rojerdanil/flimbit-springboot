package com.riseup.flimbit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.UserOffer;

@Repository
public interface UserOfferRepository extends JpaRepository<UserOffer, Long> {
}
