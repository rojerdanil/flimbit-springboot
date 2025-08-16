package com.riseup.flimbit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riseup.flimbit.entity.Movie;
import com.riseup.flimbit.entity.MovieShareSummaryInterface;
import com.riseup.flimbit.entity.User;
import com.riseup.flimbit.entity.dto.MovieDTO;

public interface MoviesRepository extends JpaRepository<Movie, Long> {
	
	void deleteByIdIn(List<Integer> moivieIds);
	
	Optional<Movie>  findByTitleIgnoreCaseAndLanguage(String title,int language);
	
	//List<Movie> findByLanguageIgnoreCaseOrderByCreatedDate(String language);
	
	@Query(value = """
			SELECT 
			    mo.id as id,
			    mo.title,
			    mo.description,
			    lan.name as language,
			    mo.budget,
			    mo.per_share_amount as perShareAmount,
			    mo.created_date as createdDate,
			    mo.updated_date as updatedDate,
			    mo.release_date as releaseDate,
			    mo.trailer_date as trailerDate,
			    COALESCE(SUM(mi.amount_invested), 0) as investedAmount,
			    ROUND(
			        CASE 
			            WHEN mo.budget > 0 THEN (COALESCE(SUM(mi.amount_invested), 0) * 100) / mo.budget 
			            ELSE 0 
			        END, 2
			    ) AS investedPercentage,
			    trailer_url as trailerUrl,
			    poster_url as posterUrl,
			    mt.name as movieTypeName,
			    ms.name as status,
			    string_agg(DISTINCT rim.name || ' : ' || mp.name, ', ') AS cast
			FROM movies mo
			LEFT JOIN movies_investment mi ON mo.id = mi.movie_id
			LEFT JOIN movie_types mt ON mt.id = mo.movie_type_id
			LEFT JOIN movie_status ms ON ms.id = mo.status_id
			LEFT JOIN movie_actors ma ON ma.movie_id = mo.id
			LEFT JOIN movie_person mp ON mp.id = ma.movie_actor_id
			LEFT JOIN roles_in_movie rim ON rim.id = ma.movie_role_id
			LEFT JOIN languages as lan on lan.id = mo.language
			WHERE mo.language = :language
			  AND mo.status = :status
			  AND EXISTS (
			      SELECT 1
			      FROM share_type s
			      WHERE s.movie_id = mo.id
			        AND s.is_active = true
			        AND NOW() BETWEEN s.start_date AND s.end_date
			  )
			GROUP BY
			    mo.id,
			    mo.title,
			    mo.description,
			    mo.language,
			    mo.budget,
			    mo.per_share_amount,
			    mo.created_date,
			    mo.updated_date,
			    mo.release_date,
			    mo.trailer_date,
			    mo.trailer_url,
			    mo.poster_url,
			    mt.name,
			    ms.name,
			    lan.name
			LIMIT :limit OFFSET :offset
			""", nativeQuery = true)

	List<MovieShareSummaryInterface> findByLanguageOrderByCreatedDate(@Param("language") int language,@Param("limit") int limt,@Param("offset") int offset,@Param("status") String status);
    
    
    
    @Query(value = """
    	    SELECT s.*,lan.name as languageName,mv.name as typeName FROM movies s	
    	    join languages lan on lan.id = s.language	
            join movie_types mv on mv.id = s.movie_type_id
    	    WHERE 
    	        (:keyword IS NULL OR :keyword = '' 
    	         OR LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
    	         OR LOWER(lan.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    	      AND (:language IS NULL OR :language = '' OR LOWER(lan.name) = LOWER(:language)) 
    	    ORDER BY created_date DESC
    	    LIMIT :limit OFFSET :offset
    	""", nativeQuery = true)
    	List<MovieDTO> findMoviesWithSearch(
    	    @Param("keyword") String keyword,
    	    @Param("limit") int limit,
    	    @Param("offset") int offset,
    	    @Param("language") String language
    	);

    @Query(value = """
    	    SELECT COUNT(s.*) FROM movies s
    	    join languages lan on lan.id = s.language	

    	    WHERE 
    	        (:keyword IS NULL OR :keyword = '' 
    	         OR LOWER(title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
    	         OR LOWER(lan.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    	      AND (:language IS NULL OR :language = '' OR LOWER(lan.name) = LOWER(:language))
    	""", nativeQuery = true)
    	long countMoviesWithSearch(
    	    @Param("keyword") String keyword,
    	    @Param("language") String language
    	);

    
    
    @Query(
    	    value = """
    	        SELECT 
    	            mo.id AS id,
    	            mo.title,
    	            mo.description,
    	            mo.language,
    	            mo.budget,
    	            mo.per_share_amount AS perShareAmount,
    	            mo.created_date AS createdDate,
    	            mo.updated_date AS updatedDate,
    	            mo.release_date AS releaseDate,
    	            mo.trailer_date AS trailerDate,
    	            COALESCE(SUM(mi.amount_invested), 0) AS investedAmount,
    	            ROUND(
    	                CASE 
    	                    WHEN mo.budget > 0 
    	                    THEN (COALESCE(SUM(mi.amount_invested), 0) * 100) / mo.budget 
    	                    ELSE 0 
    	                END, 2
    	            ) AS investedPercentage,
    	            trailer_url AS trailerUrl,
    	            poster_url AS posterUrl,
    	            mt.name AS movieTypeName,
    	            ms.name AS status,
    	            string_agg(DISTINCT rim.name || ' : ' || mp.name, ', ') AS cast
    	        FROM movies mo
    	        LEFT JOIN movies_investment mi 
    	            ON mo.id = mi.movie_id
    	        LEFT JOIN movie_types mt 
    	            ON mt.id = mo.movie_type_id
    	        LEFT JOIN movie_status ms 
    	            ON ms.id = mo.status_id
    	        LEFT JOIN movie_actors ma 
    	            ON ma.movie_id = mo.id
    	        LEFT JOIN movie_person mp 
    	            ON mp.id = ma.movie_actor_id
    	        LEFT JOIN roles_in_movie rim 
    	            ON rim.id = ma.movie_role_id
    	        WHERE mo.id = :id
    	        GROUP BY 
    	            mo.id, mo.title, mo.description, mo.language, mo.budget, mo.per_share_amount,
    	            mo.created_date, mo.updated_date, mo.release_date, mo.trailer_date,
    	            mo.trailer_url, mo.poster_url, mt.name, ms.name
    	        """,
    	    nativeQuery = true
    	)
    	List<MovieShareSummaryInterface> findMovieSummaryById(@Param("id") int id);

    
    @Query(value = """
    	    SELECT s.* FROM movies s	
    	    join languages lan on lan.id = s.language	

    	    WHERE s.language =  :language and  LOWER(s.status) = LOWER(:status)
    	        
    	""", nativeQuery = true)
	List<Movie>  getByLanguageAndStatusIgnoreCase(@Param("language")  int id,@Param("status")  String status);

    
    @Query(value = """
    	    SELECT s.* FROM movies s	
    	    join languages lan on lan.id = s.language	

    	    WHERE s.language =  :language 
    	        
    	""", nativeQuery = true)
	List<Movie>  getMovieByLanguage(@Param("language")  int id);
    
    
    @Query(value = """
    	    SELECT 
    	        mo.id as id,
    	        mo.title,
    	        mo.description,
    	        lan.name as language,
    	        mo.budget,
    	        mo.per_share_amount as perShareAmount,
    	        mo.created_date as createdDate,
    	        mo.updated_date as updatedDate,
    	        mo.release_date as releaseDate,
    	        mo.trailer_date as trailerDate,
    	        COALESCE(SUM(mi.amount_invested), 0) as investedAmount,
    	        ROUND(
    	            CASE 
    	                WHEN mo.budget > 0 THEN (COALESCE(SUM(mi.amount_invested), 0) * 100) / mo.budget 
    	                ELSE 0 
    	            END, 2
    	        ) AS investedPercentage,
    	        trailer_url as trailerUrl,
    	        poster_url as posterUrl,
    	        mt.name as movieTypeName,
    	        ms.name as status,
    	        string_agg(DISTINCT rim.name || ' : ' || mp.name, ', ') AS cast
    	    FROM movies mo
    	    LEFT JOIN movies_investment mi ON mo.id = mi.movie_id
    	    LEFT JOIN movie_types mt ON mt.id = mo.movie_type_id
    	    LEFT JOIN movie_status ms ON ms.id = mo.status_id
    	    LEFT JOIN movie_actors ma ON ma.movie_id = mo.id
    	    LEFT JOIN movie_person mp ON mp.id = ma.movie_actor_id
    	    LEFT JOIN roles_in_movie rim ON rim.id = ma.movie_role_id
    	    LEFT JOIN languages as lan on lan.id = mo.language
    	    WHERE (:language = 0 OR mo.language = :language)
    	      AND mo.status = :status
    	      AND (
    	           :search IS NULL OR :search = '' 
    	           OR LOWER(mo.title) LIKE LOWER(CONCAT('%', :search, '%')) 
    	           OR LOWER(mp.name) LIKE LOWER(CONCAT('%', :search, '%'))
    	      )
    	      AND EXISTS (
    	          SELECT 1
    	          FROM share_type s
    	          WHERE s.movie_id = mo.id
    	            AND s.is_active = true
    	            AND NOW() BETWEEN s.start_date AND s.end_date
    	      )
    	    GROUP BY
    	        mo.id,
    	        mo.title,
    	        mo.description,
    	        mo.language,
    	        mo.budget,
    	        mo.per_share_amount,
    	        mo.created_date,
    	        mo.updated_date,
    	        mo.release_date,
    	        mo.trailer_date,
    	        mo.trailer_url,
    	        mo.poster_url,
    	        mt.name,
    	        ms.name,
    	        lan.name
    	    LIMIT :limit OFFSET :offset
    	    """, nativeQuery = true)
    	List<MovieShareSummaryInterface> findByLanguageAndSearchOrderByCreatedDate(
    	    @Param("language") int language,
    	    @Param("limit") int limit,
    	    @Param("offset") int offset,
    	    @Param("status") String status,
    	    @Param("search") String search
    	);

    
    
    @Query(value = """
    	    SELECT s.*,lan.name as languageName,mv.name as typeName FROM movies s	
    	    join languages lan on lan.id = s.language	
            join movie_types mv on mv.id = s.movie_type_id
    	    WHERE  s.id = :movieId
    	        
    	""", nativeQuery = true)
    	MovieDTO findMoviesWithStatusAndTypeAndLangName(@Param("movieId") int movieId);
   
}
