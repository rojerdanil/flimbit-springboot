package com.riseup.flimbit.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.riseup.flimbit.constant.RewardTarget;
import com.riseup.flimbit.constant.RewardType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "promotion_rewards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionReward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long promotionTypeId;

    @Enumerated(EnumType.STRING)
    private RewardType rewardType;

    private BigDecimal rewardValue;

    @Enumerated(EnumType.STRING)
    private RewardTarget rewardTarget;

    private BigDecimal minInvestment;
    private Integer milestoneCount;
    private Integer rewardLimit;

    private String status;

    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
    
    String name;
}
