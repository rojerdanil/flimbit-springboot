package com.riseup.flimbit.entity.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
public interface MovieOfferFlatDto {
    Long getMovieId();
    String getMovieTitle();
    String getLanguage();
    Integer getBudget();
    Timestamp getReleaseDate();
    Timestamp getTrailerDate();
    String getStatus();
    Integer getMovieTypeId();
    Long getTotalInvestedAmount();

    Long getShareTypeId();
    String getShareTypeName();
    Integer getPricePerShare();
    Integer getTotalShares();
    Double getCompanyCommissionPercent();
    Double getProfitCommissionPercent();
    Timestamp getShareStartDate();
    Timestamp getShareEndDate();

    Long getOfferShareTypeMovieId();
    Long getOfferId();
    String getOfferName();
    Integer getOfferType();
    Timestamp getValidFrom();
    Timestamp getValidTo();
    BigDecimal getDiscountAmount();
    BigDecimal getWalletCreditAmount();
    Boolean getNoProfitCommission();
    Boolean getPromoCodeRequired();
    Boolean getNoPlatFormCommission();
    String getOfferStatus();
    String getOfferTypeName();
    String getMovType();
    long getMovShareOfferId();
    
}


