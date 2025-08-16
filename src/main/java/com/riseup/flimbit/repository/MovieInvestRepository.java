package com.riseup.flimbit.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riseup.flimbit.entity.EarningBreakInFace;
import com.riseup.flimbit.entity.InvestmentSummary;
import com.riseup.flimbit.entity.MovieInvestSummary;
import com.riseup.flimbit.entity.MovieInvestment;
import com.riseup.flimbit.entity.UserPayoutInitiation;
import com.riseup.flimbit.entity.dto.MovieInvestmentSummaryDTO;
import com.riseup.flimbit.entity.dto.MovieProfitRawDataDTO;
import com.riseup.flimbit.entity.dto.UserInvestmentSectionDTO;
import com.riseup.flimbit.entity.dto.UserInvestmentSharTypeDTO;
import com.riseup.flimbit.entity.dto.UserMoviePurchaseProjection;

public interface MovieInvestRepository extends JpaRepository<MovieInvestment, Integer> {

	List<MovieInvestment> findByUserIdAndStatusOrderByUpdatedDateDesc(int userId, String status);

	@Query(value = "SELECT COALESCE(SUM(amount_invested),0) FROM movies_investment where movie_id = ?1 and share_type_id = ?2", nativeQuery = true)
	BigDecimal getInvestedAmountByMovieIdAndShareTypeId(int movieId, int shareTypeId);

	Optional<List<MovieInvestment>> findByUserIdAndMovieId(int userId, int movieId);

	List<MovieInvestment> findByUserId(long userId);

	@Query(value = """
		    SELECT 
		        COALESCE(SUM(i.invested), 0) AS totalInvested,
		        COALESCE(SUM(COALESCE(p.totalReturn, 0)), 0) AS totalReturns,
		        COALESCE(
		            ROUND(
		                (SUM(COALESCE(p.totalReturn, 0)) * 100.0) / NULLIF(SUM(i.invested), 0),
		            2),
		        0) AS averageRoi,

		        COALESCE(COUNT(*), 0) AS projectsInvested,

		        COALESCE(SUM(CASE 
		            WHEN ms.name IN ('Released','Box Office Running','Profit Distribution','Archived') 
		            THEN 1 ELSE 0 END), 0) AS successfulReleases,

		        COALESCE(SUM(CASE 
		            WHEN ms.name IN ('Idea Stage','Pre-Production','Funding Open','Funding Closed',
		                             'Production','Post Production','Trailer Released','Coming Soon') 
		            THEN 1 ELSE 0 END), 0) AS ongoingProjects,

		        COALESCE(SUM(CASE 
		            WHEN ms.name IN ('On Hold','Cancelled') 
		            THEN 1 ELSE 0 END), 0) AS HoldReleases,

		        COALESCE(SUM(CASE 
		            WHEN ms.name IN ('Released','Box Office Running','Profit Distribution','Archived') 
		            THEN i.invested ELSE 0 END), 0) AS releasedFunds,

		        COALESCE(SUM(CASE 
		            WHEN ms.name IN ('On Hold','Cancelled') 
		            THEN i.invested ELSE 0 END), 0) AS holdingFunds,

		        COALESCE(SUM(CASE 
		            WHEN ms.name IN ('Idea Stage','Pre-Production','Funding Open','Funding Closed',
		                             'Production','Post Production','Trailer Released','Coming Soon') 
		            THEN i.invested ELSE 0 END), 0) AS ongoingFunds

		    FROM (
		        SELECT 
		            mi.user_id,
		            mi.movie_id,
		            SUM(mi.amount_invested) AS invested
		        FROM movies_investment mi
		        WHERE mi.user_id = ?1
		        GROUP BY mi.user_id, mi.movie_id
		    ) i
		    JOIN movies m ON m.id = i.movie_id
		    JOIN movie_status ms ON ms.id = m.status_id
		    LEFT JOIN (
		        SELECT 
		            py.user_id,
		            py.movie_id,
		            SUM(py.amount) AS totalReturn
		        FROM payout py
		        WHERE py.user_id = ?1            -- filter early
		        GROUP BY py.user_id, py.movie_id
		    ) p ON p.user_id = i.user_id AND p.movie_id = i.movie_id
		    """, nativeQuery = true)
		MovieInvestSummary getPortFolioSummary(long userId);


	@Query(value = """
		    SELECT 
		        mo.title AS movieName,
		        COALESCE(i.invested, 0) AS invested,
		        COALESCE(p.totalReturn, 0) AS returned,
		        COALESCE(
		            ROUND(
		                CASE 
		                    WHEN i.invested = 0 THEN 0
		                    ELSE (p.totalReturn * 100 / i.invested)
		                END, 
		            2),
		        0) AS averageRoi
		    FROM (
		        SELECT 
		            movie_id,
		            SUM(amount_invested) AS invested
		        FROM movies_investment
		        WHERE user_id = ?1
		        GROUP BY movie_id
		    ) i
		    JOIN movies mo ON mo.id = i.movie_id
		    LEFT JOIN (
		        SELECT 
		            movie_id,
		            SUM(amount) AS totalReturn
		        FROM payout
		        WHERE user_id = ?1
		        GROUP BY movie_id
		    ) p ON p.movie_id = i.movie_id
		    """, nativeQuery = true)
		List<EarningBreakInFace> getEarningBreak(long userId);


	@Query(value = "SELECT i.movie_id AS movieId, SUM(i.number_Of_Shares) AS totalShares " + "FROM movies_investment i "
			+ "WHERE i.movie_id IN :movieIds " + "GROUP BY i.movie_id", nativeQuery = true)
	List<InvestmentSummary> getShareSummaryForMovieIds(@Param("movieIds") List<Long> movieIds);

	@Query(value = """
			SELECT
			    mov.title AS movieName,
			    lang.name AS langName,
			    SUM(mi.amount_invested) AS investAmount,

			    CASE
			        WHEN SUM(CASE WHEN LOWER(mi.status) = 'inactive' THEN 1 ELSE 0 END) > 0
			            THEN 'INACTIVE'
			        ELSE 'ACTIVE'
			    END AS status,

			    us.first_name AS firstName,
			    us.last_name AS lastName,
			    COUNT(DISTINCT st.id) AS totalShareType,
			    us.phone_number AS phoneNumber,
			    SUM(mi.number_of_shares) AS totalShares,
			    mov.id AS movieId,
			    us.id AS userId,
			    mov.per_share_amount AS perShareAmount,
			    SUM(COALESCE(ism_agg.totalFreeShare, 0)) AS totalFreeShare,
			    CAST(SUM(COALESCE(ism_agg.totalDiscountAmount, 0)) AS DECIMAL(10,2)) AS totalDiscountAmount,
			    CAST(SUM(COALESCE(ism_agg.totalWalletDiscountAmount, 0)) AS DECIMAL(10,2)) AS totalWalletDiscountAmount,
			    CAST(COALESCE(py.returnAmount, 0) AS DECIMAL(10,2)) AS returnAmount,

			    MAX(mi.invested_at) AS createdDate,
			    MAX(mi.updated_date) AS updateDate

			FROM movies_investment mi
			JOIN users us ON mi.user_id = us.id
			JOIN movies mov ON mi.movie_id = mov.id	
			JOIN languages lang ON lang.id = mov.language
			JOIN share_type st ON st.id = mi.share_type_id

			-- Aggregate invest_offer_money per investment_id
			LEFT JOIN (
			    SELECT
			        invest_id,
			        SUM(COALESCE(free_share, 0)) AS totalFreeShare,
			        SUM(COALESCE(discount_amount, 0)) AS totalDiscountAmount,
			        SUM(COALESCE(wallet_amount, 0)) AS totalWalletDiscountAmount
			    FROM invest_offer_money
			    GROUP BY invest_id
			) ism_agg ON ism_agg.invest_id = mi.id

			-- Aggregate payouts per movie & user
			LEFT JOIN (
			    SELECT
			        movie_id,
			        user_id,
			        SUM(amount) AS returnAmount
			    FROM payout
			    GROUP BY movie_id, user_id
			) py ON py.movie_id = mov.id AND py.user_id = us.id

			WHERE
			    (:search IS NULL OR (
			        LOWER(lang.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
			        LOWER(us.first_name) LIKE LOWER(CONCAT('%', :search, '%')) OR
			        us.phone_number LIKE CONCAT('%', :search, '%') OR
			        LOWER(mov.title) LIKE LOWER(CONCAT('%', :search, '%')) OR
			        LOWER(mi.status) LIKE LOWER(CONCAT('%', :search, '%'))
			    ))
			    AND (:movie = 0 OR mov.id = :movie)
			    AND (:status IS NULL OR LOWER(mi.status) = LOWER(:status))
			    AND (:language = 0 OR lang.id = :language)

			GROUP BY
			    mov.id,
			    mov.title,
			    lang.name,
			    us.id,
			    us.first_name,
			    us.last_name,
			    us.phone_number,
			    mov.per_share_amount,
			    py.returnAmount

                           """, nativeQuery = true)
	Page<UserInvestmentSectionDTO> getSearchMovieInvForUserInvestSection(@Param("language") int language,
			@Param("movie") int movie, @Param("status") String status, @Param("search") String searchText,
			Pageable pageable);

	@Query(value = """
			SELECT mi.id AS id,
			       mi.user_id AS userId,
			       mi.movie_id AS movieId,
			       mi.number_of_shares AS numberOfShares,
			       mi.amount_invested AS amountInvested,
			       mi.invested_at AS investedAt,
			       mi.status AS status,
			       mi.updated_date AS updatedDate,
                   py.amount AS returnAmount,
			       st.name AS shareTypeName,
			       st.price_per_share as pricePerShare,
			       st.id as shareId
			FROM movies_investment mi
			JOIN share_type st ON mi.share_type_id = st.id
			join movies mov on mov.id = mi.movie_id
			LEFT JOIN payout py  ON py.investment_id = mi.id
			where mov.id = :movId and mi.user_id = :userId
			""", nativeQuery = true)
	Optional<List<UserInvestmentSharTypeDTO>> getInvestmentsWithShareTypeByMovId(@Param("movId") int movieId,
			@Param("userId") int userId);

	@Query(value = """
			SELECT mi.id AS id,
			       mi.user_id AS userId,
			       mi.movie_id AS movieId,
			       mi.number_of_shares AS numberOfShares,
			       mi.amount_invested AS amountInvested,
			       mi.invested_at AS investedAt,
			       mi.status AS status,
			       mi.updated_date AS updatedDate,
			       mi.return_amount AS returnAmount,
			       st.name AS shareTypeName,
			       st.price_per_share as pricePerShare
			FROM movies_investment mi
			JOIN share_type st ON mi.share_type_id = st.id
			join movies mov on mov.id = mi.movie_id
			where mov.id = :movId and mi.user_id = :userId and st.id = :shareId
			""", nativeQuery = true)
	Optional<List<UserInvestmentSharTypeDTO>> getInvestmentsForMovIdAndUserIdAndShareTypeId(@Param("movId") int movieId,
			@Param("userId") int userId, @Param("shareId") int shareId);

	Optional<MovieInvestment> findByUserIdAndMovieIdAndShareTypeId(int userId, int movieId, int shareTypeId);

	@Query(value = "SELECT COALESCE(SUM(amount_invested), 0) " + "FROM movies_investment " + "WHERE user_id = :userId "
			+ "AND EXTRACT(YEAR FROM invested_at) = EXTRACT(YEAR FROM CURRENT_DATE)", nativeQuery = true)
	BigDecimal getTotalInvestedAmountCurrentYear(@Param("userId") int userId);

	@Query(value = """
			    SELECT
			        st.id AS shareTypeId,
			        st.name AS shareTypeName,
			        SUM(mi.number_of_shares) AS totalSharesSold,

			        -- Shares where platform commission applied
			        SUM(
			            CASE
			                WHEN iom.is_no_platform_commission = TRUE
			                THEN (COALESCE(iom.free_share,0) + iom.total_share)
			                ELSE 0
			            END
			        ) AS platformCommissionAppliedShares,

			        -- Shares where profit commission applied
			        SUM(
			            CASE
			                WHEN iom.is_no_profit_commission = TRUE
			                THEN (COALESCE(iom.free_share,0) + iom.total_share)
			                ELSE 0
			            END
			        ) AS profitCommissionAppliedShares,

			        -- Discounts
			        SUM(COALESCE(iom.discount_amount, 0)) AS totalDiscountAmount,
			        SUM(COALESCE(iom.wallet_amount, 0)) AS totalWalletAmount,
			        st.company_commission_percent as platformCommision,
			        st.profit_commission_percent as profitCommision,
			                 SUM(COALESCE(iom.free_share, 0)) AS totalFreeShare,
			                  mi.id as investmentId
			    FROM movies_investment mi
			    JOIN share_type st ON st.id = mi.share_type_id
			    LEFT JOIN invest_offer_money iom ON iom.invest_id = mi.id
			    WHERE mi.movie_id = :movieId
			    GROUP BY st.id, st.name,st.company_commission_percent,mi.id
			""", nativeQuery = true)
	List<MovieProfitRawDataDTO> findMovieProfitData(int movieId);

	@Query(value = """
			SELECT
			     m.id AS movieId,
			  m.title AS movieName,
			  m.budget AS budget,
			  m.per_share_amount AS perShareAmount,
			  COALESCE(SUM(mi.amount_invested), 0) AS totalInvestedAmount,
			  COALESCE(SUM(mi.number_of_shares), 0) AS totalSharesPurchased,
			  COUNT(DISTINCT mi.user_id) AS totalInvestors
			FROM movies_investment mi
			JOIN movies m ON m.id = mi.movie_id
			WHERE mi.movie_id = :movieId
			GROUP BY m.id, m.title, m.budget, m.per_share_amount
			""", nativeQuery = true)
	MovieInvestmentSummaryDTO getMovieInvestmentSummary(@Param("movieId") int movieId);

	@Query(value = "SELECT DISTINCT user_id FROM movies_investment WHERE movie_id = :movieId AND is_processed = false LIMIT :limit OFFSET :offset", nativeQuery = true)
	List<Integer> findDistinctUserIdsByMovieIdAndIsProcessedFalse(@Param("movieId") int movieId,
			@Param("limit") int limit, @Param("offset") int offset);

	@Query(value = """
			    SELECT
			        st.id AS shareTypeId,
			        st.name AS shareTypeName,
			        SUM(mi.number_of_shares) AS totalSharesSold,

			        -- Shares where platform commission applied
			        SUM(
			            CASE
			                WHEN iom.is_no_platform_commission = TRUE
			                THEN (COALESCE(iom.free_share,0) + iom.total_share)
			                ELSE 0
			            END
			        ) AS platformCommissionAppliedShares,

			        -- Shares where profit commission applied
			        SUM(
			            CASE
			                WHEN iom.is_no_profit_commission = TRUE
			                THEN (COALESCE(iom.free_share,0) + iom.total_share)
			                ELSE 0
			            END
			        ) AS profitCommissionAppliedShares,

			        -- Discounts
			        SUM(COALESCE(iom.discount_amount, 0)) AS totalDiscountAmount,
			        SUM(COALESCE(iom.wallet_amount, 0)) AS totalWalletAmount,
			        st.company_commission_percent as platformCommision,
			        st.profit_commission_percent as profitCommision,
			              SUM(COALESCE(iom.free_share, 0)) AS totalFreeShare,
			              mi.id as investmentId
			    FROM movies_investment mi
			    JOIN share_type st ON st.id = mi.share_type_id
			    JOIN USERS us on us.id = mi.user_id
			    LEFT JOIN invest_offer_money iom ON iom.invest_id = mi.id
			    WHERE mi.movie_id = :movieId  and mi.user_id = :userId
			    GROUP BY st.id, st.name,mi.user_id,mi.id
			""", nativeQuery = true)
	List<MovieProfitRawDataDTO> findMovieProfitDataForUser(int movieId, int userId);

	@Query(value = """
			    SELECT
			        st.id AS shareTypeId,
			        st.name AS shareTypeName,
			        SUM(mi.number_of_shares) AS totalSharesSold,

			        -- Shares where platform commission applied
			        SUM(
			            CASE
			                WHEN iom.is_no_platform_commission = TRUE
			                THEN (COALESCE(iom.free_share,0) + iom.total_share)
			                ELSE 0
			            END
			        ) AS platformCommissionAppliedShares,

			        -- Shares where profit commission applied
			        SUM(
			            CASE
			                WHEN iom.is_no_profit_commission = TRUE
			                THEN (COALESCE(iom.free_share,0) + iom.total_share)
			                ELSE 0
			            END
			        ) AS profitCommissionAppliedShares,

			        -- Discounts
			        SUM(COALESCE(iom.discount_amount, 0)) AS totalDiscountAmount,
			        SUM(COALESCE(iom.wallet_amount, 0)) AS totalWalletAmount,
			        st.company_commission_percent as platformCommision,
			        st.profit_commission_percent as profitCommision,
			              SUM(COALESCE(iom.free_share, 0)) AS totalFreeShare,
			              mi.id as investmentId
			    FROM movies_investment mi
			    JOIN share_type st ON st.id = mi.share_type_id
			    JOIN USERS us on us.id = mi.user_id
			    LEFT JOIN invest_offer_money iom ON iom.invest_id = mi.id
			    WHERE mi.movie_id = :movieId  and mi.user_id = :userId
			    GROUP BY st.id, st.name,mi.user_id,mi.id
			""", nativeQuery = true)
	List<MovieProfitRawDataDTO> findMovieProfitDataForUserAndShareTypeId(int movieId, int userId);

	@Query(value = "SELECT COUNT(DISTINCT user_id) FROM movies_investment "
			+ "WHERE movie_id = :movieId AND is_processed = false", nativeQuery = true)
	int countDistinctUserIdsByMovieIdAndIsProcessedFalse(@Param("movieId") int movieId);
	
	@Query(value = """
			SELECT
			     m.id AS movieId,
			  m.title AS movieName,
			  m.budget AS budget,
			  m.per_share_amount AS perShareAmount,
			  COALESCE(SUM(mi.amount_invested), 0) AS totalInvestedAmount,
			  COALESCE(SUM(mi.number_of_shares), 0) AS totalSharesPurchased,
			  COUNT(DISTINCT mi.user_id) AS totalInvestors
			FROM movies_investment mi
			JOIN movies m ON m.id = mi.movie_id
			WHERE mi.user_id = :userId
			GROUP BY m.id, m.title, m.budget, m.per_share_amount
			""", nativeQuery = true)
	MovieInvestmentSummaryDTO getMovieInvestmentSummaryForUserId(@Param("userId") int userId);
	
	
	
	    @Query(value = """
	            SELECT
	                m.id AS movieId,
	                m.title AS movieName,
	                m.budget AS budget,
	                m.per_share_amount AS perShareAmount,
	                m.description AS description,
	                m.created_date AS createdDate,
	                m.updated_date AS updateDate,
	                m.trailer_date AS trailerDate,
	                m.poster_url AS posterUrl,
	                COALESCE(SUM(mi.amount_invested), 0) AS totalInvestedAmount,
	                COALESCE(SUM(mi.number_of_shares), 0) AS totalSharesPurchased,
	                COUNT(DISTINCT mi.user_id) AS totalInvestors,
	                ms.name AS movieStatus,
	                m.status_id AS movieStatusId,
	                mt.name AS movieType,
	                p.totalReturn AS totalReturn,
	                MAX(mi.invested_at) AS lastInvestmentDate
	            FROM movies_investment mi
	            JOIN movies m ON m.id = mi.movie_id
	            LEFT JOIN movie_status ms ON m.status_id = ms.id
	            LEFT JOIN movie_types mt ON mt.id = movie_type_id
	            LEFT JOIN (
	                SELECT 
	                    py.user_id,
	                    py.movie_id,
	                    SUM(py.amount) AS totalReturn
	                FROM payout py
	                WHERE py.user_id = :userId
	                GROUP BY py.user_id, py.movie_id
	            ) p ON p.user_id = mi.user_id AND p.movie_id = mi.movie_id
	            WHERE mi.user_id = :userId
	            GROUP BY m.id, m.title, m.budget, m.per_share_amount, m.description,
	                     m.created_date, m.updated_date, m.trailer_date, m.poster_url,
	                     ms.name, m.status_id, mt.name, p.totalReturn
	                     ORDER BY lastInvestmentDate DESC
	            """, nativeQuery = true)
	    List<UserMoviePurchaseProjection> findUserMoviePurchases(@Param("userId") int userId);
	

}
