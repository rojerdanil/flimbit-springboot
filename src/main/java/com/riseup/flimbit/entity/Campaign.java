package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "influencer_id", nullable = false)
    private int influencerId;  // Using int as per your requirement

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "platforms", columnDefinition = "text[]")
    private String[] platforms;

    @Column(name = "content_type", columnDefinition = "text[]")
    private String[] contentType;

    @Column(name = "goal")
    private String goal;

    @Column(name = "compensation_details")
    private String compensationDetails;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
}
