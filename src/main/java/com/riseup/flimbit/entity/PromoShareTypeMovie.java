package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "promo_share_type_movie")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoShareTypeMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "promo_id", nullable = false)
    private Long promoId;

    @Column(name = "share_type_id", nullable = false)
    private Long shareTypeId;

    @Column(name = "movie_id", nullable = false)
    private Long movieId;

    @Column(name = "valid_from", nullable = false)
    private Date validFrom;

    @Column(name = "valid_to", nullable = false)
    private Date validTo;

    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
}
