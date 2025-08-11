package com.riseup.flimbit.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.hibernate.annotations.Formula;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "movies_investment")
@Getter
@Setter
public class MovieInvestment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movies_investment_seq")
	@SequenceGenerator(name = "movies_investment_seq", sequenceName = "movies_investment_seq", allocationSize = 1)
	int id;
	int userId; 
	int movieId; 
	int  numberOfShares; 
	BigDecimal amountInvested;
	String status;
	Timestamp investedAt = new Timestamp(System.currentTimeMillis());
	Timestamp updatedDate = new Timestamp(System.currentTimeMillis());
	BigDecimal returnAmount;
	int shareTypeId;
	
	int totalShareNoPlatformCommission;
	
	int totalShareNoProfitCommission;

	
	@Column(name = "is_processed")
	private boolean isProcessed ;
	public BigDecimal getAmountInvested()
	{
        return amountInvested != null ? amountInvested : BigDecimal.ZERO;

	}

}
