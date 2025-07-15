package com.riseup.flimbit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.Payout;
import com.riseup.flimbit.entity.dto.PayoutDTO;
import com.riseup.flimbit.entity.dto.UserInvestmentSectionDTO;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, Integer> {

    // Optional: Add custom queries if needed
	 Payout findByInvestmentId(int investmentId);

	    List<Payout> findByUserId(int userId);

	    List<Payout> findByStatus(String status);

	    List<Payout> findByMovieId(int movieId);
	    
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
	COALESCE(py.status, 'pending') AS payStatus
	,py.method as payMethord,
	py.created_at as paidCreated,
	py.updated_at as paidUpdate,
	 mov.per_share_amount as perShareAmount
FROM movies_investment mi
JOIN users us ON mi.user_id = us.id
JOIN movies mov ON mi.movie_id = mov.id
JOIN languages lang ON lang.id = mov.language
JOIN share_type st ON st.id = mi.share_type_id
LEFT JOIN (
    SELECT 
        user_id, movie_id, 
        SUM(amount) AS amount, 
        MAX(status) AS status, 
        MAX(method) AS method,
        MAX(created_at) AS created_at,
        MAX(updated_at) AS updated_at
    FROM payout
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
    py.amount, py.status, py.method, py.created_at, py.updated_at
 ORDER BY py.updated_at   
	
""", nativeQuery = true)
	    
	    Page<PayoutDTO> getSearchMoviePayForUserPayoutSection(
	    		@Param("language") int language,
	    		@Param("movie") int movie,
	    		@Param("status") String status,
	    		@Param("search") String searchText, Pageable pageable);
	    
	    
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
	    	        COALESCE(py.amount, 0) AS payAmount,
	    	        COALESCE(py.status, 'pending') AS payStatus,
	    	        py.method AS payMethod,
	    	        py.created_at AS paidCreated,
	    	        py.updated_at AS paidUpdate,
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
	    	        payout py ON py.investment_id = mi.id AND py.user_id = us.id
	    	    where us.id = :userId  and mi.movie_id =:movId
	    	        
	    	""", nativeQuery = true)

	    List<PayoutDTO>  getPayoutForUserIdAndMovieId(@Param("userId") int userId,@Param("movId") int movId);
	    
	    Optional<List<Payout>>  findByUserIdAndMovieIdAndShareTypeIdAndInvestmentId
	                             (int userId,int movieId,int shareTypeId,int investId);
	    

	   


}