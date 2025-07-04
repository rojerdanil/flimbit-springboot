package com.riseup.flimbit.request;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class PromotionRewardRequest {

    private String rewardName;
    private Long promotionTypeId;
    private String rewardType;
    private BigDecimal rewardValue;
    private String rewardTarget;
    private Integer rewardLimit;
    private String rewardStatus;

    private Long movieId = 0L;         // Default to 0 if not applicable
    private Long shareTypeId = 0L;     // Default to 0 if not applicable

    private BigDecimal minInvestment = BigDecimal.ZERO; // Optional
    private Integer milestoneCount = 0;                 // Optional
}

