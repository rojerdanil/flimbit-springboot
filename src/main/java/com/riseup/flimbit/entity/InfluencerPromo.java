package com.riseup.flimbit.entity;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "influencer_promo")
@Getter
@Setter
public class InfluencerPromo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "promo_code_id", nullable = false)
    private int promoCodeId;

    @Column(name = "influencer_id", nullable = false)
    private int influencerId;

    @Column(name = "campaign_name")
    private String campaignName;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());

}
