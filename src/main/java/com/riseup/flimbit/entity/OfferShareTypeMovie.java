package com.riseup.flimbit.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "offer_share_type_movie")

@Getter
@Setter

public class OfferShareTypeMovie {

	 @Id
	    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "offer_share_type_movie_seq_gen")
	    @SequenceGenerator(name = "offer_share_type_movie_seq_gen", sequenceName = "offer_share_type_movie_seq", allocationSize = 1)
	    private Long id;

	    @Column(name = "offer_id", nullable = false)
	    private Long offerId;

	    @Column(name = "share_type_id", nullable = false)
	    private Long shareTypeId;

	    @Column(name = "movie_id", nullable = false)
	    private Long movieId;

	    @Column(name = "valid_from", nullable = false)
	    private Timestamp validFrom = new Timestamp(System.currentTimeMillis());

	    @Column(name = "valid_to", nullable = false)
	    private Timestamp validTo = new Timestamp(System.currentTimeMillis());

	    @Column(name = "max_users")
	    private Integer maxUsers;

	    @Column(name = "discount_amount", precision = 10, scale = 2)
	    private BigDecimal discountAmount;

	    @Column(name = "wallet_credit_amount", precision = 10, scale = 2)
	    private BigDecimal walletCreditAmount;

	    @Column(name = "no_profit_commission")
	    private Boolean noProfitCommission = false;

	    @Column(name = "no_platform_commission")
	    private Boolean noPlatFormCommission = false;
	    
	    @Column(name = "promo_code_required")
	    private Boolean promoCodeRequired =false;

	    @Column(name = "status")  
	    private String status = "unactive"; 

	    @Column(name = "created_at")
	    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

	    @Column(name = "updated_at")
	    private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
}
