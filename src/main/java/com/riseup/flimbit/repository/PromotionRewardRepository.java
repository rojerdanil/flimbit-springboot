package com.riseup.flimbit.repository;

import com.riseup.flimbit.entity.PromotionReward;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRewardRepository extends JpaRepository<PromotionReward, Long> {
    List<PromotionReward> findByPromotionTypeIdAndStatusIgnoreCase(Long promotionTypeId, String status);

    @Query("SELECT r FROM PromotionReward r WHERE " +
    	       "LOWER(r.rewardType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
    	       "LOWER(r.rewardTarget) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
    	       "LOWER(r.status) LIKE LOWER(CONCAT('%', :search, '%'))")
    	Page<PromotionReward> findBySearch(@Param("search") String search, Pageable pageable);
}
