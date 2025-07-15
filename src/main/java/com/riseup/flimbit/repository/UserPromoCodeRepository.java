package com.riseup.flimbit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.UserPromoCode;

@Repository
public interface UserPromoCodeRepository extends JpaRepository<UserPromoCode, Long> {
	
	
	@Query(value = """
		    SELECT upc.* FROM user_promo_codes upc
		    JOIN promo_codes pc ON upc.promo_id = pc.id
		    JOIN promotion_type pt ON pc.promo_type_id = pt.id
		    WHERE upc.user_id = :userId
		      AND LOWER(pt.type_code) IN (:typeCodes)
		    """, nativeQuery = true)
		List<UserPromoCode> findByUserIdAndTypeCodesIgnoreCase(
		    @Param("userId") Long userId,
		    @Param("typeCodes") List<String> typeCodes);
	
	UserPromoCode findByPromoId(long promoId);
	Optional<UserPromoCode> findFirstByPromoId(int promoId);
	
	Optional<UserPromoCode>  findByPromoIdAndRewardMapId(int promoId,int rewardMapId);


}
