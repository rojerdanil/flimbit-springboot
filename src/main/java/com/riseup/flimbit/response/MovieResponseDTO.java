package com.riseup.flimbit.response;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieResponseDTO {
    private long id;
    private String title;
    private String description;
    private String language;
    private int budget;
    private int perShareAmount;
    private Timestamp releaseDate;
    private Timestamp trailerDate;
    private int statusId;
    private int movieTypeId;
    private String posterUrl;
    private String trailerUrl;
    private String daysBeforeRelease;
    private String shareStatus;
    private int sharesSold;
    private int totalShares;
    private String shareSoldAmount; // e.g. ₹2,50,000 of ₹10,00,000
    private int fundingPercent;
    

    // Getters & setters...
}
