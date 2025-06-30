package com.riseup.flimbit.request;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class ShareTypeRequest {
	
    private Long id;
    private Integer categoryId;
    private String name;
    private String startDate;
    private String endDate;
    private Integer pricePerShare;
    private Double companyCommissionPercent;
    private Double profitCommissionPercent;
    private Integer numberOfShares;
    private Boolean isActive;
    private long movieId;
    
}
