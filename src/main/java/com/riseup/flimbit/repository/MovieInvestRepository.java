package com.riseup.flimbit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riseup.flimbit.entity.EarningBreakInFace;
import com.riseup.flimbit.entity.MovieInvestSummary;
import com.riseup.flimbit.entity.MovieInvestment;


public interface MovieInvestRepository extends JpaRepository<MovieInvestment, Long>{

	List<MovieInvestment> findByUserIdAndStatusOrderByUpdatedDateDesc(int userId,String status);
    @Query(value = "SELECT COALESCE(SUM(amount_invested),0) FROM movies_investment where movie_id = ?1", nativeQuery = true)
	int getInvestedAmountByMovieId(int movieId);
    
	Optional<MovieInvestment> findByUserIdAndMovieId(long userId,int movieId);
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
    MovieInvestSummary  getPortFolioSummary(int userId);

    @Query(value = "SELECT mo.title as movieName ,sum(mi.amount_invested) as invested ,sum(mi.return_amount) as returned"
    		+ ",ROUND(CASE WHEN SUM(mi.amount_invested) = 0 THEN 0"
    		+" ELSE (SUM(mi.return_amount) * 100 / SUM(mi.amount_invested))  END, 2) AS averageRoi"
    		+" from movies_investment mi"
    		+" LEFT JOIN movies mo on mo.id = mi.movie_id where mi.user_id = ?1 group by mo.title" ,nativeQuery = true)
    List<EarningBreakInFace> getEarningBreak(int userId);
    
 }
