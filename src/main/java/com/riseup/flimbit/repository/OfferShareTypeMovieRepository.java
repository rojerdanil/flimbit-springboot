package com.riseup.flimbit.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riseup.flimbit.entity.OfferShareTypeMovie;

public interface OfferShareTypeMovieRepository extends JpaRepository<OfferShareTypeMovie, Long> {
	
	
	
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
}
