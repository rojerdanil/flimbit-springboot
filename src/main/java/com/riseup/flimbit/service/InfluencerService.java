package com.riseup.flimbit.service;

import org.springframework.data.domain.Page;

import com.riseup.flimbit.entity.Influencer;
import com.riseup.flimbit.entity.dto.InfluencerDTO;
import com.riseup.flimbit.request.InfluencerRequest;

public interface InfluencerService {
	
	 Page<InfluencerDTO> getInfluencerDataTable(int language,String status,String searchText, int start, int length,
				String sortColumn, String sortOrder) ;
	 
	 Influencer create(InfluencerRequest request);
	 Influencer update(int id, InfluencerRequest request);
	    void delete(int id);
	    Influencer  getInfluencerById(int id); 
}
