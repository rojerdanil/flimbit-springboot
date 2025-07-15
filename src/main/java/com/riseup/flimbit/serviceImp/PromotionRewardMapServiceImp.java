package com.riseup.flimbit.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.PromotionRewardsMap;
import com.riseup.flimbit.entity.dto.PromotionRewardMapDTO;
import com.riseup.flimbit.repository.PromotionRewardsMapRepository;
import com.riseup.flimbit.service.PromotionRewardMapService;

import jakarta.transaction.Transactional;
@Service
public class PromotionRewardMapServiceImp  implements PromotionRewardMapService {
	@Autowired
	PromotionRewardsMapRepository promoRewardMapRepository;

	@Override
	public Page<PromotionRewardMapDTO> getPaginated(long promoCodeId,int start, int length, String searchText, String sortColumn,
			String sortOrder) {
		// TODO Auto-generated method stub
		 int page = start / length;
	        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
	        Pageable pageable = PageRequest.of(page, length, sort);

	        if (searchText != null && !searchText.trim().isEmpty()) {
	            return promoRewardMapRepository.getRewardDetailsByPromoCodeIdAndSearchText(promoCodeId, searchText, pageable);
	        } 
	        else {
	            return promoRewardMapRepository.findByPromoCodeId(promoCodeId, pageable);
	        }
	    }

	@Transactional
	@Override
	public void deleteById(long id) {
		// TODO Auto-generated method stub
		promoRewardMapRepository.deleteById(id);
		
	}


}


