package com.riseup.flimbit.service;

import com.riseup.flimbit.entity.PromotionReward;
import com.riseup.flimbit.request.PromotionRewardRequest;

import java.util.List;

import org.springframework.data.domain.Page;

public interface PromotionRewardService {
    List<PromotionReward> getRewardsByPromotionType(Long promotionTypeId);
    PromotionReward createReward(PromotionRewardRequest reward);
    Page<PromotionReward> getPaginated(int start, int length, String searchText, String sortColumn, String sortOrder);
    PromotionReward  getRewardById(long id);
    PromotionReward updateReward(long id,PromotionRewardRequest reward);
    void deleteById(long id);
    

}
