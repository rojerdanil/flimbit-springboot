package com.riseup.flimbit.response.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OfferDto {
    private Long offerId;
    private String offerName;
    private Integer offerType;
    private String validFrom;
    private String validTo;
    private BigDecimal discountAmount;
    private BigDecimal walletCreditAmount;
    private Boolean noProfitCommission;
    private Boolean promoCodeRequired;
    private Boolean NoPlatFormCommission;
    private String offerStatus;
    private String offerTypeName;
    private long movShareOfferId;
    
    boolean IsBuyAndGet;
    int buyQuantity;
    int freeQuantity;
    String targetAudience;
    int maxUsers;
}