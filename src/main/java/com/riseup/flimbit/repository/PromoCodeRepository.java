package com.riseup.flimbit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.PromoCode;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    PromoCode findByPromoCode(String promoCode);
}
