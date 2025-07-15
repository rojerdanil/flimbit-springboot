package com.riseup.flimbit.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.MovieShareType;
import com.riseup.flimbit.entity.OfferShareTypeMovie;
import com.riseup.flimbit.repository.MovieShareTypeRepository;
import com.riseup.flimbit.repository.OfferShareTypeMovieRepository;
import com.riseup.flimbit.request.MovieShareOfferRequest;
import com.riseup.flimbit.service.OfferShareTypeMovieService;
import com.riseup.flimbit.utility.DateUtility;

import jakarta.transaction.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class OfferShareTypeMovieServiceImpl implements OfferShareTypeMovieService {

    @Autowired
    private OfferShareTypeMovieRepository repository;
    
    @Autowired
    MovieShareTypeRepository  movieshareTypeRepo;

    @Override
    public OfferShareTypeMovie save(MovieShareOfferRequest offerReq) {
    	
    	MovieShareType movieShareType = movieshareTypeRepo.findById(offerReq.getShareTypeId())
    	                               .orElseThrow(() -> new RuntimeException("Movie shareType is not found for " + offerReq.getShareTypeId()));
    	
    	//Timestamp startDate =  DateUtility.getTimeStampFromText(offerReq.getValidFrom() + ":00.000");
    	//Timestamp endDate = DateUtility.getTimeStampFromText(offerReq.getValidTo()+ " :00.000");
    	
    	Timestamp startDate = DateUtility.getTimeStampFromText(offerReq.getValidFrom());
    	Timestamp endDate =  DateUtility.getTimeStampFromText(offerReq.getValidTo());
    	boolean isOfferWithinShareRange = DateUtility.isOfferWithinShareRange(
    			startDate, 
    			endDate, 
    			movieShareType.getCreatedDate(), movieShareType.getEndDate());
    	if(isOfferWithinShareRange)
    	{	
    		List<OfferShareTypeMovie> listExistOffer =repository.findOverlappingOffers(startDate, endDate,offerReq.getMovieId(),offerReq.getOfferId());
    		
    		if(listExistOffer.size() > 0)
    			throw new RuntimeException("This movie has  another offer in same validFrom and validTo dates");
    		
    	OfferShareTypeMovie offerShareMovie = new OfferShareTypeMovie();
    	offerShareMovie.setDiscountAmount(offerReq.getDiscountAmount());
    	offerShareMovie.setMaxUsers(offerReq.getMaxUsers());
    	offerShareMovie.setMovieId(offerReq.getMovieId());
    	offerShareMovie.setNoProfitCommission(offerReq.getNoProfitCommission());
    	offerShareMovie.setOfferId(offerReq.getOfferId());
    	offerShareMovie.setPromoCodeRequired(offerReq.getPromoCodeRequired());
    	offerShareMovie.setShareTypeId(offerReq.getShareTypeId());
    	offerShareMovie.setStatus(offerReq.getStatus());
    	offerShareMovie.setValidFrom(startDate);
    	offerShareMovie.setValidTo(endDate);
    	offerShareMovie.setWalletCreditAmount(offerReq.getWalletCreditAmount());
    	offerShareMovie.setNoPlatFormCommission(offerReq.getNoPlatFormCommission());

        return repository.save(offerShareMovie);
    	}
    	else
            throw new RuntimeException("Offer start date and end date is greater than Share start Date and and End date" + offerReq.getShareTypeId());

    	
    }

    @Override
    public List<OfferShareTypeMovie> getAll() {
        return repository.findAll();
    }

    @Override
    public OfferShareTypeMovie getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

	@Override
	public OfferShareTypeMovie update(long id, MovieShareOfferRequest offerReq) {

    	MovieShareType movieShareType = movieshareTypeRepo.findById(offerReq.getShareTypeId())
    	                               .orElseThrow(() -> new RuntimeException("Movie shareType is not found for " + offerReq.getShareTypeId()));
    	
    	//Timestamp startDate =  DateUtility.getTimeStampFromText(offerReq.getValidFrom() + ":00.000");
    	//Timestamp endDate = DateUtility.getTimeStampFromText(offerReq.getValidTo()+ " :00.000");
    	
    	Timestamp startDate = DateUtility.getTimeStampFromText(offerReq.getValidFrom());
    	Timestamp endDate =  DateUtility.getTimeStampFromText(offerReq.getValidTo());
    	boolean isOfferWithinShareRange = DateUtility.isOfferWithinShareRange(
    			startDate, 
    			endDate, 
    			movieShareType.getCreatedDate(), movieShareType.getEndDate());
    	if(isOfferWithinShareRange)
    	{	
    		List<OfferShareTypeMovie> listExistOffer =repository.findOverlappingOffersById(startDate, endDate,offerReq.getMovieId(),offerReq.getOfferId(),id);
    // for(OfferShareTypeMovie test:listExistOffer) 
    	//   System.out.println("id "+ test.getId());
    		if(listExistOffer.size() > 0)
    			throw new RuntimeException("This movie has  another offer in same validFrom and validTo dates");
    		
    	OfferShareTypeMovie offerShareMovie =  repository.findById(id)
    			   .orElseThrow(() -> new RuntimeException("Movie Share Offer is not found for id " + id));
    	
    	offerShareMovie.setDiscountAmount(offerReq.getDiscountAmount());
    	offerShareMovie.setMaxUsers(offerReq.getMaxUsers());
    	offerShareMovie.setMovieId(offerReq.getMovieId());
    	offerShareMovie.setNoProfitCommission(offerReq.getNoProfitCommission());
    	offerShareMovie.setOfferId(offerReq.getOfferId());
    	offerShareMovie.setPromoCodeRequired(offerReq.getPromoCodeRequired());
    	offerShareMovie.setShareTypeId(offerReq.getShareTypeId());
    	offerShareMovie.setStatus(offerReq.getStatus());
    	offerShareMovie.setValidFrom(startDate);
    	offerShareMovie.setValidTo(endDate);
    	offerShareMovie.setWalletCreditAmount(offerReq.getWalletCreditAmount());
    	offerShareMovie.setNoPlatFormCommission(offerReq.getNoPlatFormCommission());
        return repository.save(offerShareMovie);
    	}
    	else
            throw new RuntimeException("Offer start date and end date is greater than Share start Date and and End date" + offerReq.getShareTypeId());

	}
}
