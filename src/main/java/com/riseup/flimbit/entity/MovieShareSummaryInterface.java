package com.riseup.flimbit.entity;

import java.sql.Timestamp;

public interface MovieShareSummaryInterface {
	long getId();
	String getTitle();
	String getDescription();
	String getLanguage();
	int getBudget();
	int getPerShareAmount();
	String getStatus();
	Timestamp  getCreatedDate();
	Timestamp getUpdatedDate(); 
	Timestamp getReleaseDate();
	Timestamp getTrailerDate();
	int getInvestedAmount();
	Double getInvestedPercentage();
	String getMovieTypeName();
	String getPosterUrl();
	String getTrailerUrl();
	String getCast();

	
}
