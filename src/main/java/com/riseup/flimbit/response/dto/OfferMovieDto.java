package com.riseup.flimbit.response.dto;

import java.util.List;

import lombok.Data;

@Data
public class OfferMovieDto {
	
	private Long movieId;
    private String movieTitle;
    private String language;
    private Integer budget;
    private String releaseDate;
    private String startDate;
    private String trailerDate;
    private String status;
    private Integer movieTypeId;
    private Long totalInvestedAmount;
    private String movieType;
    private List<OfferShareTypeDto> shareTypes;

}
