package com.riseup.flimbit.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.UserPayoutInitiation;
import com.riseup.flimbit.entity.dto.PayoutDTO;
import com.riseup.flimbit.entity.dto.UserPayoutInitateStatusSummaryDTO;
import com.riseup.flimbit.entity.dto.UserPayoutInitiationDTO;

@Repository
public interface UserPayoutInitiationRepository extends JpaRepository<UserPayoutInitiation, Integer> {

	@Query(value = """
			SELECT COUNT(DISTINCT user_id) FROM (
			    SELECT user_id
			    FROM user_payout_initiation
			    WHERE movie_id = :movieId
			    GROUP BY user_id
			    HAVING SUM(CASE WHEN status = 'failed' THEN 1 ELSE 0 END) = 0
			) AS successful_users
			""", nativeQuery = true)
	int countFullySuccessfulUsers(@Param("movieId") int movieId);

	@Query(value = """
			SELECT COUNT(DISTINCT user_id) FROM (
			    SELECT user_id
			    FROM user_payout_initiation
			    WHERE movie_id = :movieId
			    GROUP BY user_id
			    HAVING SUM(CASE WHEN status = 'failed' THEN 1 ELSE 0 END) > 0
			       AND SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) > 0
			) AS partial_failure_users
			""", nativeQuery = true)
	int countPartialFailureUsers(@Param("movieId") int movieId);

	@Query(value = """
			SELECT COUNT(DISTINCT user_id) FROM (
			    SELECT user_id
			    FROM user_payout_initiation
			    WHERE movie_id = :movieId
			    GROUP BY user_id
			    HAVING SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) = 0
			       AND SUM(CASE WHEN status = 'failed' THEN 1 ELSE 0 END) > 0
			) AS fully_failed_users
			""", nativeQuery = true)
	int countFullyFailedUsers(@Param("movieId") int movieId);

	@Query(value = """
			  SELECT
			    COUNT(DISTINCT CASE WHEN failed_count = 0 THEN user_id END) AS fully_successful,
			    COUNT(DISTINCT CASE WHEN failed_count > 0 AND success_count > 0 THEN user_id END) AS partial_failure,
			    COUNT(DISTINCT CASE WHEN success_count = 0 AND failed_count > 0 THEN user_id END) AS fully_failed
			  FROM (
			    SELECT
			      user_id,
			      SUM(CASE WHEN status IN ('completed', 'initiated') and payment_status != 'failed' THEN 1 ELSE 0 END) AS success_count,
			      SUM(CASE WHEN status = 'failed' or payment_status = 'failed' THEN 1 ELSE 0 END) AS failed_count
			    FROM user_payout_initiation
			    WHERE movie_id = :movieId
			    GROUP BY user_id
			  ) AS user_status_summary
			""", nativeQuery = true)
	UserPayoutInitateStatusSummaryDTO countUserPayoutStatusSummary(@Param("movieId") int movieId);

	@Query("SELECT u FROM UserPayoutInitiation u WHERE u.movieId = :movieId AND LOWER(u.status) = 'failed'")
	Page<UserPayoutInitiation> findFailedByMovieIdIgnoreCase(@Param("movieId") int movieId, Pageable pageable);

	@Query("SELECT u FROM UserPayoutInitiation u WHERE u.movieId = :movieId AND LOWER(u.status) = 'failed'")
	int countFailedByMovieIdIgnoreCase(@Param("movieId") int movieId);

	@Query("SELECT u FROM UserPayoutInitiation u WHERE u.movieId = :movieId AND LOWER(u.status) = LOWER(:status)")
	Page<UserPayoutInitiation> findUserPayoutBymovIdandStatus(@Param("movieId") int movieId,
			@Param("status") String status, Pageable pageable);

	@Query("SELECT COUNT(u) FROM UserPayoutInitiation u " + "WHERE u.movieId = :movieId "
			+ "AND LOWER(u.status) = LOWER(:status)")
	int countUserPayoutBymovIdandStatus(@Param("movieId") int movieId, @Param("status") String status);

	@Query("SELECT COUNT(u) FROM UserPayoutInitiation u " + "WHERE u.movieId = :movieId ")
	int countUserPayoutInitBymovId(@Param("movieId") int movieId);

	@Query(value = """
				SELECT
			    mov.title AS movieName,
			    lang.name AS langName,
			    SUM(mi.amount_invested) AS investAmount,
			    SUM(mi.return_amount) AS returnAmount,
			    mi.status AS status,
			    us.first_name AS firstName,
			    us.last_name AS lastName,
			    COUNT(st.*) AS totalShareType,
			    us.phone_number AS phoneNumber,
			    SUM(mi.number_of_shares) AS totalShares,
			    mov.id AS movieId,
			    us.id AS userId,
				py.amount as payAmount,
				COALESCE(py.status, 'pending') AS payStatus,
				py.initiated_on as initiatedOn,
				py.processed_on as processedOn,
				 mov.per_share_amount as perShareAmount
			FROM movies_investment mi
			JOIN users us ON mi.user_id = us.id
			JOIN movies mov ON mi.movie_id = mov.id
			JOIN languages lang ON lang.id = mov.language
			JOIN share_type st ON st.id = mi.share_type_id
			LEFT JOIN (
			    SELECT
			        user_id, movie_id,
			        SUM(payout_amount) AS amount,
			        CASE 
                   WHEN COUNT(CASE WHEN status != 'completed' THEN 1 END) = 0 THEN 'completed'
                   ELSE 'failed'
                   END AS status,
			        MAX(initiated_on) AS initiated_on,
			        MAX(processed_on) AS processed_on
			    FROM user_payout_initiation
			    GROUP BY user_id, movie_id
			) py ON py.user_id = mi.user_id AND py.movie_id = mi.movie_id
					WHERE
					    (
					        :search IS NULL OR (
					            LOWER(lang.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
					            LOWER(us.first_name) LIKE LOWER(CONCAT('%', :search, '%')) OR
					            us.phone_number LIKE CONCAT('%', :search, '%') OR
					            LOWER(mov.title) LIKE LOWER(CONCAT('%', :search, '%')) OR
					            LOWER(py.status) LIKE LOWER(CONCAT('%', :search, '%'))
					        )
					    )
					    AND (:movie = 0 OR mov.id = :movie)
					AND ( :status IS NULL OR ( LOWER(COALESCE(py.status, 'pending')) = LOWER(:status)))
					  AND (:language = 0 OR lang.id = :language)
                 GROUP BY 
              mov.title, lang.name, mi.status, us.first_name, us.last_name, 
               us.phone_number, mov.id, us.id,
            py.amount, py.status,  py.initiated_on, py.processed_on
          ORDER BY py.processed_on  

					""", nativeQuery = true)

	Page<UserPayoutInitiationDTO> gePayoutInitiationForDataTable(@Param("language") int language, @Param("movie") int movie,
			@Param("status") String status, @Param("search") String searchText, Pageable pageable);
	
	
	
	
	
	  @Query(value = """
 SELECT 
	    	        mov.title AS movieName, 
	    	        lang.name AS langName,
	    	        mi.amount_invested AS investAmount, 
	    	        mi.return_amount AS returnAmount,
	    	        mi.status AS status,
	    	        us.first_name AS firstName,
	    	        us.last_name AS lastName,
	    	        us.phone_number AS phoneNumber,
	    	        mi.number_of_shares AS totalShares,
	    	        mov.id AS movieId,
	    	        us.id AS userId,
	    	        COALESCE(py.payout_amount, 0) AS payAmount,
	    	        COALESCE(py.status, 'pending') AS payStatus,
	    	        py.initiated_on AS initiatedOn,
	    	        py.processed_on AS processedOn,
	    	        py.remarks as remarks,
	    	        st.name as shareTypeName,
	    	        mov.per_share_amount as perShareAmount,
	    	        st.id as shareTypeId	
	    	    FROM 
	    	        movies_investment mi
	    	    JOIN 
	    	        users us ON mi.user_id = us.id
	    	    JOIN 
	    	        movies mov ON mi.movie_id = mov.id
	    	    JOIN 
	    	        languages lang ON lang.id = mov.language
	    	    JOIN 
	    	        share_type st ON st.id = mi.share_type_id
	    	    LEFT JOIN 
	    	        user_payout_initiation py ON py.investment_id = mi.id AND py.user_id = us.id
	    	    where us.id = :userId  and mi.movie_id =:movId
	    	        
	    	""", nativeQuery = true)

	    List<UserPayoutInitiationDTO>  getPayoutInitiationForUserIdAndMovieId(@Param("userId") int userId,@Param("movId") int movId);

}
