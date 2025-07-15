package com.riseup.flimbit.entity.dto;

import java.math.BigDecimal;
import java.sql.Date;

public interface PromotionRewardMapDTO {
	
	Long getRewardMappingId();
    String getPromoCode();
    String getPromotionType();
    BigDecimal getRewardValue();
    Integer getUsesLeft();
    Date getExpiryDate();
    String getRewardStatus();
    String getRewardTarget();
    BigDecimal getMinInvestment();
    Integer getMilestoneCount();
    Integer getRewardLimit();
    String getRewardType();
    Long getPromoCodeId();
    Long getPromotionTypeId();
    Long getRewardId();
    String getActivationDate();
    int getExpiryDays();

}
