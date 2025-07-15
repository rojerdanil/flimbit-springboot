package com.riseup.flimbit.response.dto;

import java.util.List;

import lombok.Data;

@Data
public class OfferShareTypeDto {
	private Long shareTypeId;
    private String shareTypeName;
    private Integer pricePerShare;
    private Integer totalShares;
    private Double companyCommissionPercent;
    private Double profitCommissionPercent;
    private String shareStartDate;
    private String shareEndDate;
    private List<OfferDto> offers;

}
