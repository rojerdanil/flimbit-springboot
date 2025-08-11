package com.riseup.flimbit.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riseup.flimbit.entity.OfferShareTypeMovie;
import com.riseup.flimbit.entity.dto.MovieOfferFlatDto;

public interface MovieShareOfferMapRepository extends JpaRepository<OfferShareTypeMovie, Long> {
	
	
	
	@Query("""
			  SELECT o FROM OfferShareTypeMovie o
			  WHERE o.movieId = :movieId
			    AND o.offerId = :offerId
			    AND (
			         o.validFrom <= :endDate AND o.validTo >= :startDate
			    )
			""")
			List<OfferShareTypeMovie> findOverlappingOffers(
			    @Param("startDate") Timestamp startDate,
			    @Param("endDate") Timestamp endDate,
			    @Param("movieId") Long movieId,
			    @Param("offerId") Long offerId
			);
	
	@Query("""
			  SELECT o FROM OfferShareTypeMovie o
			  WHERE o.movieId = :movieId
			    AND o.offerId = :offerId
			    AND (
			         o.validFrom <= :endDate AND o.validTo >= :startDate
			    )
			    and o.id != :movShareOfferId
			""")
			List<OfferShareTypeMovie> findOverlappingOffersById(
			    @Param("startDate") Timestamp startDate,
			    @Param("endDate") Timestamp endDate,
			    @Param("movieId") Long movieId,
			    @Param("offerId") Long offerId,
			    @Param("movShareOfferId") long movShareOfferId
			    
			);
    @Query("SELECT o FROM OfferShareTypeMovie o "
    		+ "WHERE o.movieId = :movieId AND o.shareTypeId = :shareTypeId AND o.status = 'active' AND o.validFrom <= CURRENT_TIMESTAMP AND o.validTo >= CURRENT_TIMESTAMP")
    List<OfferShareTypeMovie> findActiveOffers(@Param("movieId") Long movieId, @Param("shareTypeId") Long shareTypeId);

    
    
    
    
    @Query(value = """
		    SELECT
		      m.id AS movie_id,
		      m.title AS movie_title,
		      lang.name AS language,
		      m.budget,
		      m.release_date,
		      m.trailer_date,
		      m.status,
		      m.movie_type_id,
		      movType.name as movType,
		      COALESCE(mi.total_invested_amount, 0) AS total_invested_amount,

		      st.id AS share_type_id,
		      st.name AS share_type_name,
		      st.price_per_share,
		      st.number_of_shares AS total_shares,
		      st.company_commission_percent,
		      st.profit_commission_percent,
		      st.start_date AS share_start_date,
		      st.end_date AS share_end_date,

		      osm.id AS offer_share_type_movie_id,
		      o.id AS offer_id,
		      o.offer_name,
		      o.offer_type,
		      ot.name as offerTypeName,
		      osm.valid_from,
		      osm.valid_to,
		      osm.discount_amount,
		      osm.wallet_credit_amount,
		      osm.no_profit_commission,
		      osm.no_platform_commission as NoPlatFormCommission,
		      osm.promo_code_required,
		      osm.status AS offer_status,
		      osm.id  as movShareOfferId,
		      osm.buy_one_get_one as isBuyAndGet,
		      osm.buy_quantity  as buyQuantity,
		      osm.get_quantity as freeQuantity,
              o.target_audience as targetAudience,
              osm.max_users as maxUsers
		    FROM movies m

		    LEFT JOIN (
		        SELECT movie_id, SUM(amount_invested) AS total_invested_amount
		        FROM movies_investment
		        GROUP BY movie_id
		    ) mi ON mi.movie_id = m.id

		    LEFT JOIN share_type st
		      ON st.movie_id = m.id

		    LEFT JOIN offer_share_type_movie osm
		      ON osm.share_type_id = st.id AND osm.movie_id = m.id

		    LEFT JOIN offers o
		      ON o.id = osm.offer_id
		   LEFT JOIN OFFER_TYPE ot
		      ON ot.id = o.offer_type

		    LEFT JOIN languages lang
		      ON lang.id = m.language

		    LEFT JOIN movie_types movType
		     ON   movType.id  =  m.movie_type_id

		    WHERE m.id = :movieId  and st.id  = :shareTypeId
		    AND osm.status = 'active' AND osm.valid_from <= CURRENT_TIMESTAMP AND osm.valid_to >= CURRENT_TIMESTAMP 
		    ORDER BY st.id, osm.id
		""", nativeQuery = true)
List<MovieOfferFlatDto> getOfferForMovieAndShareType(@Param("movieId") int movieId , @Param("shareTypeId") int shareTypeId);
    
    
    
    
    
}
