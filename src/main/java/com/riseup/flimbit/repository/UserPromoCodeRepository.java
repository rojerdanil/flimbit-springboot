package com.riseup.flimbit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.UserPromoCode;

@Repository
public interface UserPromoCodeRepository extends JpaRepository<UserPromoCode, Long> {
}
