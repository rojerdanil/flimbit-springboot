package com.riseup.flimbit.request;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Builder

@Getter
@Setter
public class MovieShareOfferRequest {
    private Long offerId;
    private int shareTypeId;
    private Long movieId;
    private String validFrom;
    private String validTo;
    private Integer maxUsers;
    private BigDecimal discountAmount;
    private BigDecimal walletCreditAmount;
    private Boolean noProfitCommission;
    private Boolean promoCodeRequired;
    private Boolean noPlatFormCommission;
    private String status;
    
    boolean isBuyAndGet ;
    int buyCount;
    int freeCount;
}
