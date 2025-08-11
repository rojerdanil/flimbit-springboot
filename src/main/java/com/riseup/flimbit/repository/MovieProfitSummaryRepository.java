package com.riseup.flimbit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.MovieProfitSummary;

@Repository
public interface MovieProfitSummaryRepository extends JpaRepository<MovieProfitSummary, Integer> {

	List<MovieProfitSummary> findByStatus(String status);

	MovieProfitSummary findByMovieId(int movieId);

	@Query(value = """
			SELECT * FROM movie_profit_summary m
			WHERE LOWER(m.status) = 'initiated'
			  AND (m.payment_status IS NULL OR m.payment_status = '')
			  AND NOT EXISTS (
			    SELECT 1 FROM user_payout_initiation u
			    WHERE u.movie_id = m.movie_id
			  )
			ORDER BY m.created_at ASC
			LIMIT 1
			            """, nativeQuery = true)
	MovieProfitSummary findOnePendingMovie();
}
