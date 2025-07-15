package com.riseup.flimbit.repository;

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
import com.riseup.flimbit.entity.dto.UserInvestmentSectionDTO;
import com.riseup.flimbit.entity.dto.UserInvestmentSharTypeDTO;


public interface MovieInvestRepository extends JpaRepository<MovieInvestment, Integer>{

	List<MovieInvestment> findByUserIdAndStatusOrderByUpdatedDateDesc(int userId,String status);
    @Query(value = "SELECT COALESCE(SUM(amount_invested),0) FROM movies_investment where movie_id = ?1", nativeQuery = true)
	int getInvestedAmountByMovieId(int movieId);
    
	Optional<List<MovieInvestment>> findByUserIdAndMovieId(int userId,int movieId);
    List<MovieInvestment> findByUserId(long userId);
    
    @Query(value = " SELECT SUM(mi.amount_invested) AS totalInvested,SUM(mi.return_amount) AS totalReturns,"
    	      +"ROUND(CASE WHEN SUM(mi.amount_invested) = 0 THEN 0 "
              +" ELSE (SUM(mi.return_amount) * 100 / SUM(mi.amount_invested))  END, 2) AS averageRoi,"
    	      +" COUNT(DISTINCT mi.movie_id) AS projectsInvested,"
    	      +"COUNT(DISTINCT CASE WHEN ms.name IN ('Released','Box Office Running','Profit Distribution','Archived') THEN mi.movie_id END) AS successfulReleases,"
    	      +"COUNT(DISTINCT CASE WHEN ms.name IN ('Idea Stage','Pre-Production','Funding Open','Funding Closed'"
    	      +",'Production', 'Post Production','Trailer Released','Coming Soon') THEN mi.movie_id END) AS ongoingProjects"
    	      +", COUNT(DISTINCT CASE WHEN ms.name IN ('On Hold','Cancelled') THEN mi.movie_id END) AS HoldReleases "
    	      +",SUM(DISTINCT CASE WHEN ms.name IN ('Released','Box Office Running','Profit Distribution','Archived') THEN mi.amount_invested ELSE 0 END) AS releasedFunds"
    	      +", SUM(DISTINCT CASE WHEN ms.name IN ('On Hold','Cancelled') THEN mi.amount_invested ELSE 0 END) AS holdingFunds"
    	      + ",SUM(DISTINCT CASE WHEN ms.name IN ('Idea Stage','Pre-Production','Funding Open','Funding Closed',"
    	      + " 'Production', 'Post Production','Trailer Released','Coming Soon') THEN mi.amount_invested ELSE 0 END) AS ongoingFunds"
    	    +" FROM movies_investment mi JOIN movies m ON mi.movie_id = m.id "
    	   +" JOIN  movie_status ms ON m.status_id = ms.id  WHERE  mi.user_id = ?1", nativeQuery = true)
    MovieInvestSummary  getPortFolioSummary(long userId);

    @Query(value = "SELECT mo.title as movieName ,sum(mi.amount_invested) as invested ,sum(mi.return_amount) as returned"
    		+ ",ROUND(CASE WHEN SUM(mi.amount_invested) = 0 THEN 0"
    		+" ELSE (SUM(mi.return_amount) * 100 / SUM(mi.amount_invested))  END, 2) AS averageRoi"
    		+" from movies_investment mi"
    		+" LEFT JOIN movies mo on mo.id = mi.movie_id where mi.user_id = ?1 group by mo.title" ,nativeQuery = true)
    List<EarningBreakInFace> getEarningBreak(long userId);
    
    
    @Query(value = "SELECT i.movie_id AS movieId, SUM(i.number_Of_Shares) AS totalShares " +
    	       "FROM movies_investment i " +
    	       "WHERE i.movie_id IN :movieIds " +
    	       "GROUP BY i.movie_id",nativeQuery = true)
    	List<InvestmentSummary> getShareSummaryForMovieIds(@Param("movieIds") List<Long> movieIds);
    
 

    @Query(value = "SELECT mov.title AS movieName, lang.name AS langName "
    	    +",SUM(mi.amount_invested) AS investAmount, SUM(mi.return_amount) AS returnAmount " 
    		+", mi.status AS status , us.first_name as firstName,us.last_name as lastName " +
    	    ",count(st.*) as totalShareType, " +
    		"us.phone_number as phoneNumber , sum(mi.number_of_shares) as totalShares, "
    	    +"mov.id as movieId ,us.id as userId "
    		+ " FROM movies_investment mi " +
            "JOIN users us ON mi.user_id = us.id " +
            "JOIN movies mov ON mi.movie_id = mov.id " +
            "JOIN languages lang ON lang.id = mov.language " +
            "JOIN share_type st ON st.id = mi.share_type_id " +
            "WHERE "
            + " (:search IS NULL OR ("
            + "  LOWER(lang.name) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + " LOWER(us.first_name) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + " us.phone_number LIKE CONCAT('%', :search, '%') OR "
            + " LOWER(mov.title) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + " LOWER(mi.status) LIKE LOWER(CONCAT('%', :search, '%')) "
            + " )) "
            + "  AND (:movie = 0 OR mov.id = :movie) " 
            + " AND (:status IS NULL OR LOWER(mi.status) = LOWER(:status)) "
            + " AND (:language = 0 OR lang.id = :language) "
            +"GROUP BY  mov.title, lang.name, mi.status,us.id,mov.id,us.id ", 
   nativeQuery = true)
Page<UserInvestmentSectionDTO> getSearchMovieInvForUserInvestSection(
		@Param("language") int language,
		@Param("movie") int movie,
		@Param("status") String status,
		@Param("search") String searchText, Pageable pageable);


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
                   st.price_per_share as pricePerShare,
                   st.id as shareId
            FROM movies_investment mi  
            JOIN share_type st ON mi.share_type_id = st.id 
            join movies mov on mov.id = mi.movie_id  
            where mov.id = :movId and mi.user_id = :userId
            """, nativeQuery = true)
Optional<List<UserInvestmentSharTypeDTO>> getInvestmentsWithShareTypeByMovId(
		@Param("movId") int movieId,@Param("userId") int userId);

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
Optional<List<UserInvestmentSharTypeDTO>> getInvestmentsForMovIdAndUserIdAndShareTypeId(
		@Param("movId") int movieId,@Param("userId") int userId,@Param("shareId") int shareId);

    
	Optional<MovieInvestment> findByUserIdAndMovieIdAndShareTypeId(int userId,int movieId,int shareTypeId);

	


}
