package com.riseup.flimbit.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.UserReward;

@Repository
public interface UserRewardRepository extends JpaRepository<UserReward, Integer> {
    // You can define custom queries if needed, for example:
    // List<UserReward> findByUserId(Long userId);
}
