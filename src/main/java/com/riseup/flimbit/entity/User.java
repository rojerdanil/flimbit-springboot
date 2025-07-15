package com.riseup.flimbit.entity;

import java.sql.Timestamp;
import java.time.LocalTime;

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
@Table(name = "users")
@Getter
@Setter

public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
	@SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", allocationSize = 1)

	int id;
	String phoneNumber;
	String deviceId;
	String panId;
	String firstName;
	String lastName;
	String accessKey;
	String email;
	Timestamp  createdDate = new Timestamp(System.currentTimeMillis());
	Timestamp updatedDate = new Timestamp(System.currentTimeMillis());
	Timestamp lastLogin = new Timestamp(System.currentTimeMillis());
	String status;
	int language;


}
