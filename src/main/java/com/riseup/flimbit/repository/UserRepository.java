package com.riseup.flimbit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riseup.flimbit.entity.User;
import com.riseup.flimbit.entity.dto.ChartProjectionDTO;
import com.riseup.flimbit.entity.dto.DashboardMetricsDTO;
import com.riseup.flimbit.entity.dto.UserWithStatusDTO;

public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByphoneNumber(String phoneNumber);

	@Query(value = """
					    SELECT
					        us.id AS id,
					        us.phone_number AS phoneNumber,
					        us.device_id AS deviceId,
					        us.pan_id AS panId,
					        us.first_name AS firstName,
					        us.last_name AS lastName,
					        us.email AS email,
					        us.created_date AS createdDate,
					        us.updated_date AS updatedDate,
					        us.last_login AS lastLogin,
					        us.status AS status,
					        us.language AS language,

					        usst.is_phone_verified AS isPhoneVerified,
					        usst.is_email_verified AS isEmailVerified,
					        usst.is_pan_verified AS isPanVerified,
					        usst.is_names_verified AS isNamesVerified,
					        usst.is_language_verified AS isLanguageVerified,

					        lan.name AS languageName,
					        uwb.share_cash_balance as walletBalance,
					        uwb.last_updated as walletUpdate

					    FROM users us
					    JOIN user_status usst ON us.id = usst.user_id
					    JOIN languages lan ON lan.id = us.language
					    left JOIN user_wallet_balance uwb on us.id = uwb.user_id
					    WHERE
			    (
			        :search IS NULL OR (
			            LOWER(lan.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
			            LOWER(us.first_name) LIKE LOWER(CONCAT('%', :search, '%')) OR
			            us.phone_number LIKE CONCAT('%', :search, '%') OR
			            LOWER(us.status) LIKE LOWER(CONCAT('%', :search, '%'))
			        )
			    )
			AND ( :status IS NULL OR ( LOWER(us.status) = LOWER(:status)))
			  AND (:language = 0 OR lan.id = :language)



					    """, nativeQuery = true)
	Page<UserWithStatusDTO> fetchAllUsersWithStatus(@Param("language") int language, @Param("movie") int movie,
			@Param("status") String status, @Param("search") String searchText, Pageable pageable);

	@Query(value = """
			SELECT

			(SELECT COUNT(*) as userCount FROM users) ,
			  (SELECT COUNT(*)  as movInvestCount FROM movies_investment),
			  (SELECT COALESCE(SUM(amount_invested), 0)  as amtInvestSum FROM movies_investment),
			  (SELECT COUNT(*) as investInActiveCount FROM movies_investment WHERE status = 'INACTIVE') ,
			  (SELECT COUNT(*) as totalPayout FROM payout) ,
			  (SELECT COALESCE(SUM(amount), 0) as totalPayOutAmt FROM payout) ,
			  (SELECT COUNT(*) as movCount FROM movies)  ,
			  (SELECT COUNT(*) as shareCount FROM share_type )
			""", nativeQuery = true)
	DashboardMetricsDTO getDashboardMetrics();

	@Query(value = """
			 WITH months AS (
			     SELECT to_char(date_trunc('month', CURRENT_DATE) - (interval '1 month' * (i)),'Mon') AS month,
			            to_char(date_trunc('month', CURRENT_DATE) - (interval '1 month' * (i)),'MM') AS month_no
			     FROM generate_series(0, 11) AS t(i)
			 ),
			 monthly_data AS (
			     SELECT to_char(invested_at, 'Mon') AS month,
			            to_char(invested_at, 'MM') AS month_no,
			            SUM(amount_invested) AS total_investment
			     FROM movies_investment
			     GROUP BY to_char(invested_at, 'Mon'), to_char(invested_at, 'MM')
			 )
			 SELECT m.month, COALESCE(md.total_investment, 0) AS totalInvestment
			 FROM months m
			 LEFT JOIN monthly_data md ON m.month_no = md.month_no
			 ORDER BY m.month_no::int
			""", nativeQuery = true)
	List<ChartProjectionDTO> getInvestedChart();
	
	
	@Query(value = """
		    SELECT 
		        TO_CHAR(d, 'Mon') AS month,
		        COALESCE(COUNT(u.id), 0) AS total_investment
		    FROM (
		        SELECT date_trunc('month', dd)::date AS d
		        FROM generate_series(
		            date_trunc('year', CURRENT_DATE),
		            date_trunc('year', CURRENT_DATE) + interval '11 months',
		            interval '1 month'
		        ) dd
		    ) months
		    LEFT JOIN users u ON date_trunc('month', u.created_date) = months.d
		    GROUP BY d
		    ORDER BY d
		""", nativeQuery = true)
		List<ChartProjectionDTO> getMonthlyUserRegistrationChart();
	
	

}
 