package com.riseup.flimbit.serviceImp;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.Partner;
import com.riseup.flimbit.entity.dto.PartnerDTO;
import com.riseup.flimbit.repository.PartnerRepository;
import com.riseup.flimbit.request.PartnerRequest;
import com.riseup.flimbit.service.PartnerService;

import jakarta.transaction.Transactional;

@Service
public class PartnerServiceImpl implements PartnerService {

    @Autowired
    private PartnerRepository partnerRepository;

    @Transactional
    @Override
    public Partner addPartner(PartnerRequest request) {
        Partner partner = new Partner();
        mapRequestToEntity(request, partner);
        partner.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        partner.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return partnerRepository.save(partner);
    }

    @Override
    public Partner updatePartner(int id, PartnerRequest request) {
        Partner partner = partnerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Partner not found "+id ));
        mapRequestToEntity(request, partner);
        partner.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return partnerRepository.save(partner);
    }

    @Override
    public List<Partner> getAllPartners() {
        return partnerRepository.findAll();
    }

    @Override
    public Partner getPartnerById(int id) {
        return partnerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Partner not found " + id));
    }

    @Override
    public void deletePartner(int id) {
        partnerRepository.deleteById(id);
    }

    private void mapRequestToEntity(PartnerRequest request, Partner partner) {
        partner.setName(request.getName());
        partner.setType(request.getType());
        partner.setContactPerson(request.getContactPerson());
        partner.setPhone(request.getPhone());
        partner.setEmail(request.getEmail());
        partner.setAddress(request.getAddress());
        partner.setLogoUrl(request.getLogoUrl());
        partner.setDescription(request.getDescription());
        partner.setStatus(request.getStatus());
        partner.setLanguageId(request.getLanguageId());
    }

	@Override
	public Page<PartnerDTO> getFilteredPartners(int language, String status, String searchText, int start, int length,
			String sortColumn, String sortOrder,String type) {
		// TODO Auto-generated method stub
		
		 int page = start / length;
	        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
	        Pageable pageable = PageRequest.of(page, length, sort);
		  
		return partnerRepository.getFilterPartnerDataTable(
		        language,
		        status,
		        searchText,
		        pageable,
		        type
		    );
	}
}
