package com.riseup.flimbit.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.InfluencerPromo;

@Repository
public interface InfluencerPromoRepository extends JpaRepository<InfluencerPromo, Integer> {
}