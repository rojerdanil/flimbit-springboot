package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "user_offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "offer_id", nullable = false)
    private Long offerId;

    @Column(name = "redeemed_at")
    private Timestamp redeemedAt = new Timestamp(System.currentTimeMillis());
}
