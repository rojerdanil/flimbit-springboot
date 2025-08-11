package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "movie_payout_status_history")
@Getter
@Setter
public class MoviePayoutStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Relation with movie_profit_summary table
    @Column(name = "movie_profit_id", nullable = false)
    private int movieProfitSummaryId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;  // You can use MovieProfitPayoutStatus Enum instead of String

    @Column(name = "reason", length = 255)
    private String reason;



    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
    
    
    int approverId;
}
