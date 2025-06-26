package com.riseup.flimbit.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class MovieRequest {
	long  id;
	String title;
	String description;
	String language;
	int budget;
	String releaseDate;
	String trailerDate;
	int status;
	int perShareAmount;
	int movieTypeId;
	String posterUrl;
	String trailerUrl;
     String shareStartTime;




	
}
