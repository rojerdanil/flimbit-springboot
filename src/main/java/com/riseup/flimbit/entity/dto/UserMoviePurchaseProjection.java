package com.riseup.flimbit.entity.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public interface UserMoviePurchaseProjection {

    int getMovieId();
    String getMovieName();
    BigDecimal getBudget();
    int getPerShareAmount();
    String getDescription();
    Timestamp getCreatedDate();
    Timestamp getUpdateDate();
    Timestamp getTrailerDate();
    String getPosterUrl();

    BigDecimal getTotalInvestedAmount();
    int getTotalSharesPurchased();
    int getTotalInvestors();

    String getMovieStatus();
    int getMovieStatusId();
    String getMovieType();
    BigDecimal getTotalReturn();
    Timestamp getLastInvestmentDate();
}
