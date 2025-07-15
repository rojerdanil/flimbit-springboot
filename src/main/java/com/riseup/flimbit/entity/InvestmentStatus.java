package com.riseup.flimbit.entity;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "investment_status")
@Getter
@Setter
public class InvestmentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int investmentId;  // Foreign key reference to MovieInvestment (intended for simplicity)

    private String status;  // e.g., "Active", "Inactive", "Repayable"
    private String description;  // Description or reason for the status change
    private Timestamp updatedAt =  new Timestamp(System.currentTimeMillis());
}
