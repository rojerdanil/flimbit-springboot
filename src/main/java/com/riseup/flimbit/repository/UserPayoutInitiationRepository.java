package com.riseup.flimbit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.UserPayoutInitiation;

@Repository
public interface UserPayoutInitiationRepository  extends JpaRepository<UserPayoutInitiation, Integer> {
}
