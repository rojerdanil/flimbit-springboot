package com.riseup.flimbit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.ReferralTracking;

import java.util.List;
@Repository
public interface ReferralTrackingRepository extends JpaRepository<ReferralTracking, Long> {
    List<ReferralTracking> findByReferrerUserId(Long referrerUserId);
    List<ReferralTracking> findByReferredUserId(Long referredUserId);
}
