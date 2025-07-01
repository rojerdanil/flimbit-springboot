package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "promo_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "promo_code", nullable = false, unique = true)
    private String promoCode;

    @Column(name = "promo_type", nullable = false)
    private String promoType;

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
}
