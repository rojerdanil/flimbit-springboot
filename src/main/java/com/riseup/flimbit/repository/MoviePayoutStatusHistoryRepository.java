package com.riseup.flimbit.repository;

import com.riseup.flimbit.entity.MoviePayoutStatusHistory;
import com.riseup.flimbit.entity.dto.MoviePayoutStatusHistoryDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoviePayoutStatusHistoryRepository extends JpaRepository<MoviePayoutStatusHistory, Integer> {

	@Query(
		    value = """
		        SELECT 
		            h.id AS id,
		            h.status AS status,
		            h.reason AS reason,
		            h.created_at AS createdAt,
		            a.name AS approverName
		        FROM movie_payout_status_history h
		        LEFT JOIN admin_users a ON h.approver_id = a.id
		        WHERE h.movie_profit_id = :movieProfitId
		        ORDER BY h.created_at DESC
		        """,
		    nativeQuery = true)
		List<MoviePayoutStatusHistoryDTO> findHistoryByMovieProfitId(@Param("movieProfitId") int movieProfitId);
	
	
	@Query(
		    value = """
		        SELECT 
		            h.id AS id,
		            h.status AS status,
		            h.reason AS reason,
		            h.created_at AS createdAt,
		            a.name AS approverName
		        FROM movie_payout_status_history h
		        LEFT JOIN admin_users a ON h.approver_id = a.id
		        WHERE h.movie_profit_id = :movieProfitId
		        ORDER BY h.created_at DESC
		        LIMIT 1
		        """,
		    nativeQuery = true)
		MoviePayoutStatusHistoryDTO findLatestHistoryByMovieProfitId(@Param("movieProfitId") int movieProfitId);
	}
