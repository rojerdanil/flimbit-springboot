package com.riseup.flimbit.entity.dto;

import java.sql.Timestamp;

public interface MovieDTO {
	
	
	long getId();
	String getTitle();
	String getDescription();
	int getLanguage();
	int getBudget();
	int getPerShareAmount();
	Timestamp  getCreatedDate(); 
	Timestamp getUpdatedDate(); 
	Timestamp getReleaseDate();
	Timestamp getTrailerDate();
	int getStatusId();
	int getMovieTypeId();
	String getPosterUrl();
	String getTrailerUrl();
    String getStatus();
    String getLanguageName();
    String getTypeName();


}
