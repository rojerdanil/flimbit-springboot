package com.riseup.flimbit.utility;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.riseup.flimbit.constant.RewardTarget;
import com.riseup.flimbit.constant.RewardType;
import com.riseup.flimbit.entity.PromoCode;
import com.riseup.flimbit.entity.PromotionReward;
import com.riseup.flimbit.entity.dto.MovieOfferFlatDto;
import com.riseup.flimbit.request.PromoRewardMapRequest;
import com.riseup.flimbit.request.PromotionRewardRequest;
import com.riseup.flimbit.response.dto.OfferDto;
import com.riseup.flimbit.response.dto.OfferMovieDto;
import com.riseup.flimbit.response.dto.OfferShareTypeDto;

public class MapperUtility {
	

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static String format(Timestamp ts) {
        return ts != null ? formatter.format(ts.toLocalDateTime()) : null;
    }
	
	public static PromotionReward readPromoRewardEntityFromRequest(PromotionReward reward, PromotionRewardRequest req) {

	    if (req.getPromotionTypeId() != null) {
	        reward.setPromotionTypeId(req.getPromotionTypeId());
	    }

	    if (req.getRewardType() != null) {
	        reward.setRewardType(RewardType.valueOf(req.getRewardType().toUpperCase()));
	    }

	    if (req.getRewardValue() != null) {
	        reward.setRewardValue(req.getRewardValue());
	    }

	    if (req.getRewardTarget() != null) {
	        reward.setRewardTarget(RewardTarget.valueOf(req.getRewardTarget().toUpperCase()));
	    }

	    if (req.getMinInvestment() != null) {
	        reward.setMinInvestment(req.getMinInvestment());
	    }

	    if (req.getMilestoneCount() != null) {
	        reward.setMilestoneCount(req.getMilestoneCount());
	    }

	    if (req.getRewardLimit() != null) {
	        reward.setRewardLimit(req.getRewardLimit());
	    }

	    if (req.getRewardStatus() != null && !req.getRewardStatus().isBlank()) {
	        reward.setStatus(req.getRewardStatus());
	    }
	    
	    if (req.getRewardName() != null && !req.getRewardName().isBlank()) {
	        reward.setName(req.getRewardName());
	    }
	    


	    return reward;
	}

	
public static String getNewOROldByNotEmpty(String newStr , String OldStr)
{
	String output = OldStr;
	if(newStr != null && newStr.isEmpty() == false)
		output = newStr;
	
	return output;

	
}
public static OfferMovieDto groupMovieOfferData(List<MovieOfferFlatDto> flatList) {
    if (flatList == null || flatList.isEmpty()) return null;

    OfferMovieDto movie = new OfferMovieDto();
    Map<Long, OfferShareTypeDto> shareMap = new LinkedHashMap<>();

    for (MovieOfferFlatDto row : flatList) {

        // Populate MovieDto only once
        if (movie.getMovieId() == null) {
            movie.setMovieId(row.getMovieId());
            movie.setMovieTitle(row.getMovieTitle());
            movie.setLanguage(row.getLanguage());
            movie.setBudget(row.getBudget());
            movie.setReleaseDate(format(row.getReleaseDate()));
          //  movie.setStartDate(format(row.getStartDate()));
            movie.setTrailerDate(format(row.getTrailerDate()));
            movie.setStatus(row.getStatus());
            movie.setMovieTypeId(row.getMovieTypeId());
            movie.setTotalInvestedAmount(row.getTotalInvestedAmount());
            movie.setMovieType(row.getMovType());
            movie.setShareTypes(new ArrayList<>());
        }

        // Handle ShareTypeDto grouping
        OfferShareTypeDto shareType = shareMap.get(row.getShareTypeId());
        if (shareType == null) {
            shareType = new OfferShareTypeDto();
            shareType.setShareTypeId(row.getShareTypeId());
            shareType.setShareTypeName(row.getShareTypeName());
            shareType.setPricePerShare(row.getPricePerShare());
            shareType.setTotalShares(row.getTotalShares());
            shareType.setCompanyCommissionPercent(row.getCompanyCommissionPercent());
            shareType.setProfitCommissionPercent(row.getProfitCommissionPercent());
            shareType.setShareStartDate(format(row.getShareStartDate()));
            shareType.setShareEndDate(format(row.getShareEndDate()));
            shareType.setOffers(new ArrayList<>());

            shareMap.put(row.getShareTypeId(), shareType);
        }

        // Add offer if exists
        if (row.getOfferId() != null) {
            OfferDto offer = new OfferDto();
            offer.setOfferId(row.getOfferId());
            offer.setOfferName(row.getOfferName());
            offer.setOfferType(row.getOfferType());
            offer.setValidFrom(format(row.getValidFrom()));
            offer.setValidTo(format(row.getValidTo()));
            offer.setDiscountAmount(row.getDiscountAmount());
            offer.setWalletCreditAmount(row.getWalletCreditAmount());
            offer.setNoProfitCommission(row.getNoProfitCommission());
            offer.setPromoCodeRequired(row.getPromoCodeRequired());
            offer.setOfferStatus(row.getOfferStatus());
            offer.setOfferTypeName(row.getOfferTypeName());
            offer.setMovShareOfferId(row.getMovShareOfferId());
            offer.setNoPlatFormCommission(row.getNoPlatFormCommission());
            shareType.getOffers().add(offer);
        }
    }

    movie.getShareTypes().addAll(shareMap.values());
    return movie;
}
  public  static  PromoCode MapPromoCodeRequestToEntity(PromoRewardMapRequest request,PromoCode entity)

  {
	 
       LocalDate futureDate = LocalDate.now().plusDays(request.getExpiryDays());
      // entity.setPromoTypeId(request.getPromoTypeId());
       entity.setStatus(request.getStatus());
      
       return entity;
	  
	  
  }
}
