package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "announcements")
@Getter
@Setter
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    private String message;

    @Column(name = "language_id")
    private int languageId = 0;

    @Column(name = "start_datetime")
    private Timestamp startDatetime;

    @Column(name = "valid_until")
    private Timestamp validUntil;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "link_url")
    private String linkUrl;

    private String priority = "NORMAL"; // LOW, NORMAL, HIGH

    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
    
    private boolean notifyEmail;
    private boolean notifyPush;
    @Column(name = "notify_sms")
    private boolean notifySMS;
    private boolean notifyInApp;

}
