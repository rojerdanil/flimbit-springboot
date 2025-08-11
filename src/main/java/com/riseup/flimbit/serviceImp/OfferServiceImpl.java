package com.riseup.flimbit.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.Offer;
import com.riseup.flimbit.entity.dto.MovieOfferFlatDto;
import com.riseup.flimbit.entity.dto.OfferDTO;
import com.riseup.flimbit.repository.OfferRepository;
import com.riseup.flimbit.request.OfferRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.dto.OfferMovieDto;
import com.riseup.flimbit.service.OfferService;
import com.riseup.flimbit.utility.MapperUtility;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class OfferServiceImpl implements OfferService {

    @Autowired
    private OfferRepository offerRepository;

    @Override
    public Offer create(OfferRequest offerReq) {
    	Optional<Offer> offerX = offerRepository.findByOfferNameIgnoreCase(offerReq.getOfferName());
    		if(offerX.isPresent())
    		{
    			 throw new RuntimeException("Offer name is already available " +offerReq.getOfferName());
    		}
    			
    	Offer offer  = new Offer();
    	offer.setOfferName(offerReq.getOfferName());
    	offer.setOfferType(offerReq.getOfferType());
    	offer.setStatus(offerReq.getStatus());
    	offer.setTargetAudience(offerReq.getTargetAudience());
        return offerRepository.save(offer);
    }

    @Override
    public List<Offer> findAll() {
        return offerRepository.findAll();
    }

    @Override
    public Offer findById(Long id) {
        return offerRepository.findById(id)
        		.orElseThrow(() -> new RuntimeException("Offer is not found for id :" +id));
    }

    @Transactional
    @Override
    public CommonResponse delete(Long id) {
        offerRepository.deleteById(id);
    	return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_SUCCESS).build();

    }

	@Override
	public Page<OfferDTO> getPagedOfferTypes(int start, int length, String search, String sortColumn,
			String sortOrder) {
		// TODO Auto-generated method stub
		
	    	 int page = start / length;
	         Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
	         Pageable pageable = PageRequest.of(page, length, sort);
	        return   offerRepository.getFilterPartnerDataTable(search,pageable);
	       
	}

	@Override
	public Offer update(Long id,OfferRequest offerReq)
 {
		// TODO Auto-generated method stub
		Optional<Offer> existOfferOpt = offerRepository.findByOfferNameIgnoreCase(offerReq.getOfferName());
				
		
		  if(existOfferOpt.isPresent())
		  {
			  if(id != existOfferOpt.get().getId())
			  {
				  throw new RuntimeException("Name is already available");

			  }
			 
				  
		  }
		  Offer exitOffer = offerRepository.findById(id)
				   .orElseThrow(() -> new RuntimeException("Offer id is not available "+id));

		
		  exitOffer.setOfferName(MapperUtility.getNewOROldByNotEmpty(offerReq.getOfferName(), exitOffer.getOfferName()));
		  exitOffer.setOfferType(offerReq.getOfferType());
		  exitOffer.setStatus(MapperUtility.getNewOROldByNotEmpty(offerReq.getStatus(), exitOffer.getStatus()));
		  exitOffer.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		  return offerRepository.save(exitOffer);
		  
	}

	@Override
	public OfferMovieDto getMovieOffer(long movieId) {
		// TODO Auto-generated method stub
		
		List<MovieOfferFlatDto>  listDto = offerRepository.getMovieOfferSection(movieId);
		System.out.println("sixsf" +listDto.size());
		if(listDto == null || listDto.size() == 0)
			throw new RuntimeException("movie is not found " +movieId);
		return MapperUtility.groupMovieOfferData(listDto);
	}
	
  

}
