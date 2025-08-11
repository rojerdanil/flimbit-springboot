package com.riseup.flimbit.entity.dto;

import java.math.BigDecimal;

public interface MovieInvestmentSummaryDTO {
    int getMovieId();
    String getMovieName();
    BigDecimal getTotalInvestedAmount();
    Integer getTotalSharesPurchased();
    Integer getTotalInvestors();
    BigDecimal getBudget();
    BigDecimal getPerShareAmount();
}
