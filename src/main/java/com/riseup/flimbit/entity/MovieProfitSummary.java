	package com.riseup.flimbit.entity;
	
	import jakarta.persistence.*;
	import lombok.Getter;
	import lombok.Setter;
	
	import java.math.BigDecimal;
	import java.sql.Timestamp;
	
	@Entity
	@Table(name = "movie_profit_summary")
	@Getter
	@Setter
	public class MovieProfitSummary {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private int id;
	
	    @Column(name = "movie_id", nullable = false)
	    private int movieId;
	
	    @Column(name = "total_profit", nullable = false)
	    private BigDecimal totalProfit = BigDecimal.ZERO;
	
	    @Column(name = "status", nullable = false, length = 20)
	    private String status = "PENDING";
	
	    @Column(name = "remarks", length = 255)
	    private String remarks;
	
	    @Column(name = "created_at")
	    private Timestamp createdAt = new Timestamp(System.currentTimeMillis()) ;
	
	    @Column(name = "updated_at")
	    private Timestamp updatedAt = new Timestamp(System.currentTimeMillis()) ;
	    
	    private String paymentStatus;
	}
