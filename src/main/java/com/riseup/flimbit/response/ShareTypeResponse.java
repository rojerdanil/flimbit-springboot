package com.riseup.flimbit.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShareTypeResponse {
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
    private String createdDate;
    private String updatedDate;
    private Long movieId;
    private int soldShare;
    private int soldAmount;
    private double companyCommission;
    private double companyProfitCommission;
    private int budget;
    private int totalShare;
    private int perShareAmount;
}