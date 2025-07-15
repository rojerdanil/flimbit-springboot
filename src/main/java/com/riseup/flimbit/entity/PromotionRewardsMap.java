package com.riseup.flimbit.entity;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "promotion_reward_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionRewardsMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "promo_code_id", nullable = false)
    private int promoCodeId;

    @Column(name = "reward_id", nullable = false)
    private int rewardId;

    @Column(name = "promotion_type_id", nullable = false)
    private int promotionTypeId;

    @Column(name = "uses_left")
    private Integer usesLeft = 0;

    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;

    @Column(name = "status")
    private String status = "Active";

    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
   
    @Column(name = "activation_date")
    private Timestamp activationDate;
    
}
