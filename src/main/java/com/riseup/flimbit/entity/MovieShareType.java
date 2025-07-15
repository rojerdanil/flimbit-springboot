package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "share_type")
@Getter
@Setter
public class MovieShareType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "share_type_seq")
    @SequenceGenerator(name = "share_type_seq", sequenceName = "share_type_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    private String name;
    private Timestamp startDate = new Timestamp(System.currentTimeMillis());
    private Timestamp endDate =new Timestamp(System.currentTimeMillis());
    private Integer pricePerShare;

    private Double companyCommissionPercent;
    private Double profitCommissionPercent;

    private Integer numberOfShares;
    private Boolean isActive;

    private Timestamp createdDate = new Timestamp(System.currentTimeMillis());
    private Timestamp updatedDate = new Timestamp(System.currentTimeMillis());

    @Column(name = "movie_id", nullable = false)
    private Long movieId;
}
