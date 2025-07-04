package com.riseup.flimbit.serviceImp;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.PromotionType;
import com.riseup.flimbit.repository.PromotionTypeRepository;
import com.riseup.flimbit.request.PromotionTypeRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.PromotionTypeService;
import com.riseup.flimbit.utility.CommonUtilty;

import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class PromotionTypeServiceImpl implements PromotionTypeService {

    @Autowired
    private PromotionTypeRepository promotionTypeRepository;
    
    

    @Override
    public Page<PromotionType> getPaginated(int start, int length, String searchText, String sortColumn, String sortOrder) {
        int page = start / length;
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
        Pageable pageable = PageRequest.of(page, length, sort);

        if (searchText != null && !searchText.trim().isEmpty()) {
            return promotionTypeRepository.findByTypeCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    searchText, searchText, pageable
            );
        }

        return promotionTypeRepository.findAll(pageable);
    }
    
    
    @Transactional
    @Override
    public CommonResponse save(PromotionTypeRequest promotionTypeReq) {
    	
    	
    	if(promotionTypeReq.isEdit())
    	{
    		// update

    		PromotionType proType = promotionTypeRepository.findById(promotionTypeReq.getId())
                    .orElseThrow(() -> new RuntimeException("Promotion Type is not found for id" + promotionTypeReq.getId()));
    		proType = CommonUtilty.convertToEntity(promotionTypeReq,proType);
    		promotionTypeRepository.save(proType);
    		
    	}
    	else 
    	{

    		
    		PromotionType proType = new PromotionType();
    		proType = CommonUtilty.convertToEntity(promotionTypeReq,proType);
    		promotionTypeRepository.save(proType);


    	}
    	return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_SUCCESS).build();
    }

    @Transactional
    @Override
    public CommonResponse deleteById(Long id) {
        promotionTypeRepository.deleteById(id);
    	return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_SUCCESS).build();

    }


	@Override
	public List<PromotionType> findAllRecords() {
		// TODO Auto-generated method stub
      return   promotionTypeRepository.findAll();

	}
}

