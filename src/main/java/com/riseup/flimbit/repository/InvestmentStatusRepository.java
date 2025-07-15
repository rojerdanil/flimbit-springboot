package com.riseup.flimbit.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.InvestmentStatus;

@Repository
public interface InvestmentStatusRepository extends JpaRepository<InvestmentStatus, Long> {
    // Custom queries, if any, go here
}