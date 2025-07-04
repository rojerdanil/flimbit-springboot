package com.riseup.flimbit.serviceImp;


import com.riseup.flimbit.entity.PromotionReward;
import com.riseup.flimbit.repository.PromotionRewardRepository;
import com.riseup.flimbit.request.PromotionRewardRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.PromotionRewardService;
import com.riseup.flimbit.utility.MapperUtility;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionRewardServiceImpl implements PromotionRewardService {

    @Autowired
    private PromotionRewardRepository promotionRewardRepository;

    @Override
    public List<PromotionReward> getRewardsByPromotionType(Long promotionTypeId) {
        return promotionRewardRepository.findByPromotionTypeIdAndStatusIgnoreCase(promotionTypeId, "Active");
    }

   
    @Override
    public Page<PromotionReward> getPaginated(int start, int length, String searchText, String sortColumn, String sortOrder) {
        int page = start / length;
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
        Pageable pageable = PageRequest.of(page, length, sort);

        if (searchText != null && !searchText.trim().isEmpty()) {
            return promotionRewardRepository.findBySearch(searchText, pageable);
        }

        return promotionRewardRepository.findAll(pageable);
    }
    @Transactional
    @Override
    public PromotionReward createReward(PromotionRewardRequest rewardReq)
    {
    	 PromotionReward promEnity = new PromotionReward();
    	 promEnity = MapperUtility.readPromoRewardEntityFromRequest(promEnity, rewardReq);
    	return promotionRewardRepository.save(promEnity);
    }


	@Override
	public PromotionReward getRewardById(long id) {
		// TODO Auto-generated method stub
		return promotionRewardRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("PromoReward is not found for id :" +id));
		 
	}

    @Transactional
	@Override
	public PromotionReward updateReward(long id,PromotionRewardRequest rewardReq) {
		
		// TODO Auto-generated method stub
		PromotionReward proReward = promotionRewardRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("PromoReward is not found for id :" +id));
		proReward = MapperUtility.readPromoRewardEntityFromRequest(proReward, rewardReq);

    	return promotionRewardRepository.save(proReward);
	}


	@Override
	public void deleteById(long id) {
		// TODO Auto-generated method stub
		promotionRewardRepository.deleteById(id);
		
	}

}
