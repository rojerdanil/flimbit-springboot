package com.riseup.flimbit.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.riseup.flimbit.entity.Partner;
import com.riseup.flimbit.entity.dto.AnnouncementTableDTO;
import com.riseup.flimbit.entity.dto.PartnerDTO;
import com.riseup.flimbit.request.PartnerRequest;

public interface PartnerService {
    Partner addPartner(PartnerRequest request);
    Partner updatePartner(int id, PartnerRequest request);
    List<Partner> getAllPartners();
    Partner getPartnerById(int id);
    void deletePartner(int id);
    
    Page<PartnerDTO> getFilteredPartners(int language,String status,String searchText, int start, int length,
			String sortColumn, String sortOrder,String type) ;
    
}
