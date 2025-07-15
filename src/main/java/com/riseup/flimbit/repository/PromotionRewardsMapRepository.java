package com.riseup.flimbit.repository;

import com.riseup.flimbit.entity.PromotionRewardsMap;
import com.riseup.flimbit.entity.dto.PromotionRewardMapDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRewardsMapRepository extends JpaRepository<PromotionRewardsMap, Long> {

    List<PromotionRewardsMap> findByPromoCodeId(Long promoCodeId);

    List<PromotionRewardsMap> findByRewardId(Long rewardId);

    boolean existsByPromoCodeIdAndRewardId(Long promoCodeId, Long rewardId);
    Optional<PromotionRewardsMap>  findByPromoCodeIdAndPromotionTypeIdAndRewardId(long promoCodeId,
    		long promoTypeId,long rewardId);
    
   
    
    
    @Query("SELECT prm.id as rewardMappingId, " +
 	       "pc.promoCode as promoCode, " +
 	       "pt.typeCode as promotionType, " +
 	       "pr.rewardValue as rewardValue, " +
 	       "prm.usesLeft as usesLeft, " +
 	       "prm.expiryDate as expiryDate, " +
 	       "prm.status as rewardStatus, " +
 	       "pr.rewardTarget as rewardTarget, " +
	       "pr.rewardType as rewardType ," +
 	       "pr.minInvestment as minInvestment, " +
 	       "pr.milestoneCount as milestoneCount, " +
 	       "pr.rewardLimit as rewardLimit, " +
 	       "prm.promoCodeId as promoCodeId ,"+
 	       "prm.promotionTypeId as promotionTypeId ,"+
 	        "prm.rewardId as rewardId ,"+
  	       "pt.expiryDays as expiryDays ,"+
	       "prm.activationDate as activationDate "+
 	       "FROM PromotionRewardsMap prm " +
 	       "JOIN PromoCode pc ON prm.promoCodeId = pc.id " +
 	       "JOIN PromotionType pt ON prm.promotionTypeId = pt.id " +
 	       "JOIN PromotionReward pr ON prm.rewardId = pr.id " +
          "WHERE prm.promoCodeId = :promoCodeId")
    
    Page<PromotionRewardMapDTO> findByPromoCodeId(@Param("promoCodeId") Long promoCodeId, Pageable pageable);

    
  /*  @Query("SELECT pr FROM PromotionRewardsMap pr WHERE pr.promoCodeId = :promoCodeId " +
            "AND LOWER(pr.status) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    	Page<PromotionRewardsMap> searchByPromoCodeIdAndText(
    	    @Param("promoCodeId") Long promoCodeId,
    	    @Param("searchText") String searchText,
    	    Pageable pageable); */

    @Query("SELECT prm.id as rewardMappingId, " +
    	       "pc.promoCode as promoCode, " +
    	       "pt.typeCode as promotionType, " +
    	       "pr.rewardValue as rewardValue, " +
    	       "prm.usesLeft as usesLeft, " +
    	       "prm.expiryDate as expiryDate, " +
    	       "prm.status as rewardStatus, " +
    	       "pr.rewardTarget as rewardTarget, " +
    	       "pr.minInvestment as minInvestment, " +
    	       "pr.milestoneCount as milestoneCount, " +
    	       "pr.rewardLimit as rewardLimit, " +
    	       "pr.rewardType as rewardType ," +
    	       "prm.promoCodeId as promoCodeId ,"+
     	       "prm.promotionTypeId as promotionTypeId ,"+
     	       "prm.rewardId as rewardId ,"+
     	       "pt.expiryDays as expiryDays ,"+
               "prm.activationDate as activationDate "+
    	       "FROM PromotionRewardsMap prm " +
    	       "JOIN PromoCode pc ON prm.promoCodeId = pc.id " +
    	       "JOIN PromotionType pt ON prm.promotionTypeId = pt.id " +
    	       "JOIN PromotionReward pr ON prm.rewardId = pr.id " +
    	       "WHERE prm.promoCodeId = :promoCodeId " +
    	       "AND (LOWER(pc.promoCode) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
    	       "OR LOWER(pr.rewardTarget) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
    	       "OR LOWER(pt.typeCode) LIKE LOWER(CONCAT('%', :searchText, '%')))") 
    	Page<PromotionRewardMapDTO> getRewardDetailsByPromoCodeIdAndSearchText(
    	    @Param("promoCodeId") Long promoCodeId, 
    	    @Param("searchText") String searchText, 
    	    Pageable pageable);
    
}
