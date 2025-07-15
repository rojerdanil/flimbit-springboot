package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "referral_tracking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReferralTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int referrerUserId;

    private int referredUserId;

    private String promoCode;

    private BigDecimal creditAmount;

    private String creditedTo; // 'Referrer' or 'Referred'

    private Boolean shareCashApplied;

    private LocalDateTime createdAt;
}
