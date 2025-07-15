package com.riseup.flimbit.serviceImp;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.OfferType;
import com.riseup.flimbit.repository.OfferTypeRepository;
import com.riseup.flimbit.request.OfferTypeRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.OfferTypeService;
import com.riseup.flimbit.utility.MapperUtility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfferTypeServiceImpl implements OfferTypeService {

    @Autowired
    private OfferTypeRepository repository;

    @Override
    public OfferType createOfferType(OfferTypeRequest offerTypeReq) {
        if (repository.existsByName(offerTypeReq.getName())) {
            throw new RuntimeException("Offer type with name already exists");
        }
        OfferType offerType = new OfferType();
        offerType.setName(offerTypeReq.getName());
        offerType.setDescription(offerTypeReq.getDescription());
        return repository.save(offerType);
    }

    @Override
    public List<OfferType> getAllOfferTypes() {
        return repository.findAll();
    }

    @Override
    public Optional<OfferType> getOfferTypeById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public OfferType updateOfferType(Integer id, OfferTypeRequest updated) {
    	  
    	System.out.println("commming");
    	Optional<OfferType> existingOpt = repository.findByNameIgnoreCase(updated.getName());
        if(existingOpt.isPresent())
        {
        	OfferType existing = existingOpt.get();
        	System.out.println(id + " "+existing.getId());

        	if(id !=existing.getId())
                throw new RuntimeException("Offer type with name already exists");
        	
        		
        }
        OfferType  currentOffer = repository.findById(id)
        		                .orElseThrow(() ->  new RuntimeException("Offer type with name already exists"));
    	
    	
        currentOffer.setName(MapperUtility.getNewOROldByNotEmpty(updated.getName(), currentOffer.getName()));
        currentOffer.setDescription(MapperUtility.getNewOROldByNotEmpty(updated.getDescription(), currentOffer.getDescription()));
        return repository.save(currentOffer);
    }

    @Override
    public CommonResponse deleteOfferType(Integer id) {
        repository.deleteById(id);
    	return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_SUCCESS).build();

    }

    @Override
	public  Page<OfferType> getPagedOfferTypes(int start, int length, String search, String sortColumn, String sortOrder)
   
    {
    	 int page = start / length;
         Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
         Pageable pageable = PageRequest.of(page, length, sort);
        if (search != null && !search.isEmpty()) {
            return repository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            return repository.findAll(pageable);
        }
    }

}
