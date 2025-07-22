package com.riseup.flimbit.entity;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "user_reward")
public class UserReward {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_user_reward_id")
    @SequenceGenerator(name = "seq_user_reward_id", sequenceName = "seq_user_reward_id", allocationSize = 1)
    private int id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "promotion_reward_id", nullable = false)
    private int promotionRewardId;

    @Column(name = "credited_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal creditedAmount = BigDecimal.ZERO;

    @Column(name = "credited_at", nullable = false)
    private Timestamp creditedAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "reference_note", length = 255)
    private String referenceNote;

}


