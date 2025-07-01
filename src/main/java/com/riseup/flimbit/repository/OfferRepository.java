package com.riseup.flimbit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.Offer;
@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
}
