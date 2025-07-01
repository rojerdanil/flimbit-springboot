package com.riseup.flimbit.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MovieEntityResponse {
	long id;
	String title;
	String description;
	String language;
	int budget;
	int perShareAmount;
	String  createdDate;
	String updatedDate ;
	String  releaseDate ;
	String trailerDate ;

	int statusId;
	int movieTypeId;
	String posterUrl;
	String trailerUrl;
	String status;


}
