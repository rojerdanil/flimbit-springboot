package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_offer_id")
	@SequenceGenerator(name = "seq_offer_id", sequenceName = "seq_offer_id", allocationSize = 1)
    private Long id;

    @Column(name = "offer_name", nullable = false)
    private String offerName;

    @Column(name = "offer_type", nullable = false)
    private int  offerType;

    @Column(name = "target_audience")
    private String targetAudience;

    private String status ;

    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
}
