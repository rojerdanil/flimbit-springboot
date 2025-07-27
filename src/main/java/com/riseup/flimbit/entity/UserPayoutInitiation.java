package com.riseup.flimbit.entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_payout_initiation")
@Getter
@Setter
public class UserPayoutInitiation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;
    private int investmentId;
    private int movieId;
    private int shareTypeId;

    private LocalDate eligibleDate;

    private BigDecimal payoutAmount;

    private String status;

    private Timestamp initiatedOn;
    private Timestamp processedOn;

    private String remarks;
} 