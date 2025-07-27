package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_users")
@Data
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", columnDefinition = "TEXT")
    private String passwordHash;

    private int roleId;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt =  new Timestamp(System.currentTimeMillis());
    
    @Column(name = "last_login")
    private Timestamp lastLogin;

    @Column(name = "is_verified")
    private boolean isVerified;
    
    @Column(length = 15)
    private String phone;
    
    @Column(name = "is_email_verified")
    private boolean isEmailVerified;
    
    @Column(name = "is_phone_verified")
    private boolean isPhoneVerified;
    
    @Column(name = "token", columnDefinition = "TEXT")
    private String token;
    
    @Column(name = "refreshToken", columnDefinition = "TEXT")
    private String refreshToken;

    @Column(name = "device_id")
    private String deviceId;
}
