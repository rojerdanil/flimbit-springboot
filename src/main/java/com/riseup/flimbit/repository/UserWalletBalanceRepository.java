package com.riseup.flimbit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.UserWalletBalance;

import java.util.Optional;
@Repository
public interface UserWalletBalanceRepository extends JpaRepository<UserWalletBalance, Long> {
    Optional<UserWalletBalance> findByUserId(Long userId);
}
