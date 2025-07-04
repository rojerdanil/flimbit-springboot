package com.riseup.flimbit.utility;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.riseup.flimbit.constant.RewardTarget;
import com.riseup.flimbit.constant.RewardType;
import com.riseup.flimbit.entity.PromotionReward;
import com.riseup.flimbit.request.PromotionRewardRequest;

public class MapperUtility {
	
	public static PromotionReward readPromoRewardEntityFromRequest(PromotionReward reward, PromotionRewardRequest req) {

	    if (req.getPromotionTypeId() != null) {
	        reward.setPromotionTypeId(req.getPromotionTypeId());
	    }

	    if (req.getRewardType() != null) {
	        reward.setRewardType(RewardType.valueOf(req.getRewardType().toUpperCase()));
	    }

	    if (req.getRewardValue() != null) {
	        reward.setRewardValue(req.getRewardValue());
	    }

	    if (req.getRewardTarget() != null) {
	        reward.setRewardTarget(RewardTarget.valueOf(req.getRewardTarget().toUpperCase()));
	    }

	    if (req.getMinInvestment() != null) {
	        reward.setMinInvestment(req.getMinInvestment());
	    }

	    if (req.getMilestoneCount() != null) {
	        reward.setMilestoneCount(req.getMilestoneCount());
	    }

	    if (req.getRewardLimit() != null) {
	        reward.setRewardLimit(req.getRewardLimit());
	    }

	    if (req.getRewardStatus() != null && !req.getRewardStatus().isBlank()) {
	        reward.setStatus(req.getRewardStatus());
	    }
	    
	    if (req.getRewardName() != null && !req.getRewardName().isBlank()) {
	        reward.setName(req.getRewardName());
	    }
	    


	    return reward;
	}

	

}
