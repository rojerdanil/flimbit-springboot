package com.riseup.flimbit.entity;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_status")
@Getter
@Setter
public class UserStatus {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_status_seq")
	@SequenceGenerator(name = "user_status_seq", sequenceName = "user_status_seq", allocationSize = 1)
	int id;
	int userId;
	boolean isPhoneVerified;
	boolean isEmailVerified;
	boolean isPanVerified;
	boolean isNamesVerified;
	boolean isLanguageVerified;



}
