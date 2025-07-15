package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_wallet_balance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWalletBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private int userId;

    @Column(name = "share_cash_balance", nullable = false)
    private BigDecimal shareCashBalance = BigDecimal.ZERO;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();
}
