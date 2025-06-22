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
@Table(name = "movies")
@Getter
@Setter
public class Movie {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movies_seq")
	@SequenceGenerator(name = "movies_seq", sequenceName = "movies_seq", allocationSize = 1)
	long id;
	String title;
	String description;
	String language;
	int budget;
	int perShareAmount;
	Timestamp  createdDate = new Timestamp(System.currentTimeMillis());
	Timestamp updatedDate = new Timestamp(System.currentTimeMillis());
	Timestamp releaseDate = null;
	Timestamp trailerDate = null;

	int statusId;
	int movieTypeId;
	String posterUrl;
	String trailerUrl;


}
