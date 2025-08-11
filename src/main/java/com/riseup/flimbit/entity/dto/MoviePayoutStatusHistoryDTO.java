package com.riseup.flimbit.entity.dto;

public interface MoviePayoutStatusHistoryDTO {

    int getId();
    String getStatus();
    String getReason();
    String getCreatedAt();
    String getApproverName();
}
