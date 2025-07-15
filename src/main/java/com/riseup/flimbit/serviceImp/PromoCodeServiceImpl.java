package com.riseup.flimbit.serviceImp;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.PromoCode;
import com.riseup.flimbit.entity.PromotionRewardsMap;
import com.riseup.flimbit.repository.PromoCodeRepository;
import com.riseup.flimbit.repository.PromotionRewardsMapRepository;
import com.riseup.flimbit.repository.UserPromoCodeRepository;
import com.riseup.flimbit.request.PromoCodeRequest;
import com.riseup.flimbit.request.PromoRewardMapRequest;
import com.riseup.flimbit.service.PromoCodeService;
import com.riseup.flimbit.utility.DateUtility;
import com.riseup.flimbit.utility.MapperUtility;

import jakarta.transaction.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PromoCodeServiceImpl implements PromoCodeService {

    @Autowired
    private PromoCodeRepository repository;

    @Autowired
    private PromotionRewardsMapRepository rewardMapRepo;
    
    @Autowired
    private UserPromoCodeRepository userPromoCodeRepo;
    
    @Transactional
    @Override
    public PromoCode savePromoCodeRewards(PromoRewardMapRequest promoCodeReq) {
    	
    	if(promoCodeReq.getPromoCodeUsage().equalsIgnoreCase("new"))
    	{
    		PromoCode promoCodeexit =repository.findByPromoCode(promoCodeReq.getPromoCode())
    	  .orElseThrow(() ->   new RuntimeException("Promo code is not founds: " + promoCodeReq.getPromoCode()));
    	   
    		
    		Optional<PromotionRewardsMap> optExit =  rewardMapRepo.findByPromoCodeIdAndPromotionTypeIdAndRewardId(promoCodeexit.getId(), 
    				promoCodeReq.getPromoTypeId(),
    				promoCodeReq.getRewardId());
    		
    		if(optExit.isPresent())
    	            throw new RuntimeException("Promo code and promo type and reward id is  already exist: " + promoCodeReq.getPromoCode());

 
    	
    		
    		
    		
    		
    	   if (promoCodeReq.getExpiryDays() == null) {
    		    throw new IllegalArgumentException("Expiry days must not be null");
    		}
 
    		
    		LocalDate futureDate = LocalDate.now().plusDays(1);
    		Date sqlExpiryDate = Date.valueOf(futureDate); // java.sql.Date

    		// Step 1: Save PromoCode
 
    		// Step 2: Calculate expiry date
    	
    		// Step 3: Save mapping
    		PromotionRewardsMap entity = new PromotionRewardsMap();
 
    		entity.setExpiryDate(sqlExpiryDate);
    		entity.setPromotionTypeId(promoCodeReq.getPromoTypeId());
    		entity.setStatus(promoCodeReq.getStatus());
    		entity.setUsesLeft(promoCodeReq.getUsesLeft());
    		entity.setPromoCodeId(promoCodeexit.getId());
            entity.setRewardId(promoCodeReq.getRewardId());
            entity.setPromotionTypeId(promoCodeReq.getPromoTypeId());
            
            if(promoCodeReq.getActivationDate() != null)
            {
            	entity.setActivationDate(DateUtility.getTimeStampFromText(promoCodeReq.getActivationDate()));
            }
            
            
            rewardMapRepo.save(entity);

    		// Step 4: Return saved promo code
            promoCodeexit.setUpdatedAt( new Timestamp(System.currentTimeMillis()));
    		return repository.save(promoCodeexit) ;
    	
    	
    	

    	
    	}
    	// return repository.save(promoCode);
    	else if(promoCodeReq.getPromoCodeUsage().equalsIgnoreCase("exist"))
    	{
    		PromoCode promoCodeexit = repository.findByPromoCode(promoCodeReq.getPromoCode())
    	    	    .orElseThrow(() -> new RuntimeException("Promo code not found: " + promoCodeReq.getPromoCode()));
    	
    		Optional<PromotionRewardsMap> optExit =  rewardMapRepo.findByPromoCodeIdAndPromotionTypeIdAndRewardId(promoCodeexit.getId(), 
    				promoCodeReq.getPromoTypeId(),
    				promoCodeReq.getRewardId());
    		
    		if(!optExit.isPresent())
    	            throw new RuntimeException("Promo code and promo type and reward id is not already exist: " + promoCodeReq.getPromoCode());

    		
    		if(promoCodeReq.getActivationDate() != null || !promoCodeReq.getActivationDate().isEmpty())
    		{
    			System.out.println("activation date is not null");
    			 
    			PromotionRewardsMap  proRewardMap = optExit.get();
    			if(proRewardMap.getActivationDate() != null)
    			{
    				int promoId =  proRewardMap.getPromoCodeId();
    				int reMapId = (int) proRewardMap.getId();
    			userPromoCodeRepo.findByPromoIdAndRewardMapId(promoId,reMapId)
    			.ifPresent(p -> {
    				 throw new RuntimeException("Reward already used by users");
    			});
    			
    			}
    		}
    		
    		PromotionRewardsMap exitPromotionRewardsMap = optExit.get();
        	  
            // if(promoCodeReq.getStatus().equalsIgnoreCase(exitPromotionRewardsMap.getStatus()))
 	          //  throw new RuntimeException("Same status can not be changed: " + promoCodeReq.getStatus());

    	    
               exitPromotionRewardsMap.setStatus(promoCodeReq.getStatus());
               
               if(promoCodeReq.getActivationDate() != null)
               {
            	   exitPromotionRewardsMap.setActivationDate(DateUtility.getTimeStampFromText(promoCodeReq.getActivationDate()));
               }
               
    	       promoCodeexit.setUpdatedAt( new Timestamp(System.currentTimeMillis()));
    	       exitPromotionRewardsMap.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    	       rewardMapRepo.save(exitPromotionRewardsMap);

    	       return repository.save(promoCodeexit) ;



    		
    	
    	}	
    	   
    	else if(promoCodeReq.getPromoCodeUsage() ==null || promoCodeReq.getPromoCodeUsage().isEmpty())
    		 throw new RuntimeException("Promo code usage type can not be empty ");
    	else
   		 throw new RuntimeException("Promo code not meet condition ");

    	
    }

    @Override
    public List<PromoCode> getAll() {
        return repository.findAll();
    }

    @Override
    public PromoCode getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Optional<PromoCode> getByCode(String code) {
        return repository.findByPromoCode(code);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

	@Override
	public Page<PromoCode> getPaginated(int start, int length, String searchText, String sortColumn, String sortOrder) {
		// TODO Auto-generated method stub
		 int page = start / length;
	        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
	        Pageable pageable = PageRequest.of(page, length, sort);

	        if (searchText != null && !searchText.trim().isEmpty()) {
	            return repository.searchByCodeOrType(searchText, pageable);
	        }

	        return repository.findAll(pageable);

	}

	@Transactional
	@Override
	public PromoCode savePromoCode(PromoCodeRequest promoRequst) {
		// TODO Auto-generated method stub
		repository.findByPromoCode(promoRequst.getPromoCode())
		            .ifPresent(p -> { throw new RuntimeException("promocode is already available "+promoRequst.getPromoCode()) ;
		            });
		
		PromoCode promoCode = new PromoCode();
		promoCode.setPromoCode(promoRequst.getPromoCode());
		promoCode.setStatus(promoRequst.getStatus());
		promoCode.setUserType(promoRequst.getPromoType());
		
		return repository.save(promoCode);
	}
	@Transactional
	@Override
	public PromoCode updatePromoCode(PromoCodeRequest promoRequst) {
		// TODO Auto-generated method stub
		if(promoRequst.getId() == 0)
			throw new RuntimeException("pleease check id can not be 0");
		PromoCode promoCode = repository.findByPromoCode(promoRequst.getPromoCode())
		   .orElseThrow(() -> new RuntimeException("Promocode is not found"+ promoRequst.getPromoCode() ) );
		if(promoCode.getId() != promoRequst.getId())
			throw new RuntimeException("Promo id and Promo code is not same in db");

		promoCode.setStatus(promoRequst.getStatus());
		promoCode.setUserType(promoRequst.getPromoType());
		promoCode.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		return repository.save(promoCode);
	}
}
