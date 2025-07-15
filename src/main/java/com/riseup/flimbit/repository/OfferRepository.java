package com.riseup.flimbit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.Offer;
import com.riseup.flimbit.entity.OfferType;
import com.riseup.flimbit.entity.dto.MovieOfferFlatDto;
@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
	
    Page<Offer> findByOfferNameContainingIgnoreCase(String name, Pageable pageable);
    
    Optional<Offer> findByOfferNameIgnoreCase(String name);
    
    
    
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
              osm.id  as movShareOfferId

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

            WHERE m.id = :movieId
            ORDER BY st.id, osm.id
        """, nativeQuery = true)
        List<MovieOfferFlatDto> getMovieOfferSection(@Param("movieId") Long movieId);

}
