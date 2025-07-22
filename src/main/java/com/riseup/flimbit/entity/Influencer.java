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
@Table(name = "influencers")
@Getter
@Setter
public class Influencer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Use BIGSERIAL equivalent in PostgreSQL
	private int id;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@Column(name = "phone_number", length = 15)
	private String phoneNumber;

	@Column(name = "social_media_handle")
	private String socialMediaHandle;

	@Column(name = "created_at", updatable = false)
	private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

	@Column(name = "updated_at")
	private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
	
	String status;
	
	int languageId;
	
	String promoCode;
}
