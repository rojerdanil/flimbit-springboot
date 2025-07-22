package com.riseup.flimbit.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "partner")
@Getter
@Setter
public class Partner {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String type;
    private String contactPerson;
    private String phone;
    private String email;	
    private String address;
    private String logoUrl;
    private String description;
    private String status;
    private int languageId;
    @Column(name = "last_login_date")
    private Timestamp lastLoginDate;

    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt =new Timestamp(System.currentTimeMillis());

}
