package com.riseup.flimbit.entity.dto;
public interface DashboardMetricsDTO {
    Long getUserCount();
    Long getMovInvestCount();
    Double getAmtInvestSum();
    Long getInvestInActiveCount();
    Long getTotalPayout();
    Double getTotalPayOutAmt();
    Long getMovCount();
    Long getShareCount();
}
