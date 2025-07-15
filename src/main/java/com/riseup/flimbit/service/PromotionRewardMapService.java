package com.riseup.flimbit.service;

import org.springframework.data.domain.Page;

import com.riseup.flimbit.entity.PromotionRewardsMap;
import com.riseup.flimbit.entity.dto.PromotionRewardMapDTO;

public interface PromotionRewardMapService {
	
    public Page<PromotionRewardMapDTO> getPaginated(long promocode ,int start, int length, String searchText, String sortColumn, String sortOrder) ;
    public void deleteById(long id);


}
