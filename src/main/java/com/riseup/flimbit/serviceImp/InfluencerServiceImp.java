package com.riseup.flimbit.serviceImp;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.StatusEnum;
import com.riseup.flimbit.constant.TargetAudience;
import com.riseup.flimbit.entity.Announcement;
import com.riseup.flimbit.entity.Influencer;
import com.riseup.flimbit.entity.PromoCode;
import com.riseup.flimbit.entity.dto.InfluencerDTO;
import com.riseup.flimbit.repository.InfluencerRepository;
import com.riseup.flimbit.repository.PromoCodeRepository;
import com.riseup.flimbit.request.AnnouncementRequest;
import com.riseup.flimbit.request.InfluencerRequest;
import com.riseup.flimbit.service.InfluencerService;
import com.riseup.flimbit.utility.DateUtility;

import jakarta.transaction.Transactional;

@Service
public class InfluencerServiceImp implements InfluencerService {

	@Autowired
	InfluencerRepository influenRepository;
	
	@Autowired
	PromoCodeRepository promoRepository;
	
	@Override
	public Page<InfluencerDTO> getInfluencerDataTable(int language, String status, String searchText, int start,
			int length, String sortColumn, String sortOrder) {
		// TODO Auto-generated method stub

	    int page = start / length;
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
        Pageable pageable = PageRequest.of(page, length, sort);
	
		return influenRepository.getDataTableInfluencer(language, status, searchText, pageable);
	}

	@Override
	@Transactional
	public Influencer create(InfluencerRequest request) {
		// TODO Auto-generated method stub
	    promoRepository.findByPromoCode(request.getPromoCode().trim())
		   .ifPresent(s -> {
			   throw new RuntimeException("Promocode is already available "+ request.getPromoCode());
			   
		   });
	    
	    Influencer influencer = new Influencer();
	    copyFromRequest(influencer, request);
	    PromoCode promoCode = new PromoCode();
	    promoCode.setPromoCode(influencer.getPromoCode());
	    promoCode.setStatus(StatusEnum.ACTIVE.name().toLowerCase());
	    promoCode.setUserType(TargetAudience.INFFLUENCER.name().toLowerCase());
	    promoRepository.save(promoCode);
	    
		return influenRepository.save(influencer);
	}

	@Override
	public Influencer update(int id, InfluencerRequest request) {
		// TODO Auto-generated method stub
		Influencer influencer = influenRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("influencer is not found " + id));
		
	    if(!request.getPromoCode().trim().equalsIgnoreCase(influencer.getPromoCode().toLowerCase()))
		    throw new RuntimeException("promocode is not smae in db");
	    	
	   copyFromRequest(influencer, request);
		influencer.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		return influenRepository.save(influencer);
	}

	@Transactional
	@Override
	public void delete(int id) {
		// TODO Auto-generated method stub
		influenRepository.deleteById(id);
		
	}
	
	private void copyFromRequest(Influencer inf, InfluencerRequest req) {
                         inf.setEmail(req.getEmail());
                         inf.setFirstName(req.getFirstName());
                         inf.setLanguageId(req.getLanguageId());
                         inf.setLastName(req.getLastName());
                         inf.setPhoneNumber(req.getPhoneNumber());
                         inf.setSocialMediaHandle(req.getSocialMediaHandle());
                         inf.setStatus(req.getStatus());
                         inf.setPromoCode(req.getPromoCode());

}

	@Override
	public Influencer getInfluencerById(int id) {
		// TODO Auto-generated method stub
		
		
		return influenRepository.findById(id).orElseThrow(() -> new RuntimeException("Influencer is not found by given Id "+id));
	}
}