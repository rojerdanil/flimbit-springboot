package com.riseup.flimbit.entity;


import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "user_promo_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPromoCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private int  userId;

    @Column(name = "promo_id", nullable = false)
    private int promoId;

    @Column(name = "used_at")
    private Timestamp usedAt = new Timestamp(System.currentTimeMillis());
    @Column(name = "reward_map_id")
    int rewardMapId;
}
