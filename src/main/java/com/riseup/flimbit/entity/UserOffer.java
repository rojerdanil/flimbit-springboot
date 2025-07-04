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
	    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_offer_seq_gen")
	    @SequenceGenerator(name = "user_offer_seq_gen", sequenceName = "seq_user_offer_id", allocationSize = 1)
	    private Long id;

	    @Column(name = "user_id", nullable = false)
	    private Long userId;

	    @Column(name = "redeemed_at")
	    private Timestamp redeemedAt = new Timestamp(System.currentTimeMillis());

	    @Column(name = "status", length = 20)
	    private String status = "UNUSED";

	    @Column(name = "bonus_credited")
	    private Boolean bonusCredited = false;

	    @Column(name = "investment_id")
	    private Long investmentId;

	    @Column(name = "offer_share_id")
	    private Long offerShareId;
}
