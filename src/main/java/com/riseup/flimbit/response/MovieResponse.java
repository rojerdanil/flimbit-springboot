package com.riseup.flimbit.response;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Getter
@Setter
public class MovieResponse {
	long id;
	String title;
	String description;
	String language;
	int budget;
	int perShareAmount;
	String  createdDate;
	String updatedDate ;
	String releaseDate ;
	String trailerDate ;
	String status;
	int investedAmount;
	Double investedPercentage;
	String movieType;
	String posterUrl;
	String trailerUrl;
	String actorDetails;

}
