package com.riseup.flimbit.repository;

import com.riseup.flimbit.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Integer> {
}
