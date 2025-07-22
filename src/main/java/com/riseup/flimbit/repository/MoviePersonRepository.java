package com.riseup.flimbit.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.riseup.flimbit.entity.MoviePerson;
import com.riseup.flimbit.entity.dto.ActorDTO;
import com.riseup.flimbit.request.MoviePersonRequest;

public interface MoviePersonRepository extends JpaRepository<MoviePerson, Integer> {
	
	Optional<MoviePerson> findByNameIgnoreCaseAndLanguage(String name,int language);
	List<MoviePerson>  findByLanguage(int language);
	
	@Query(value = """
            SELECT
			    p.id as id,
			    p.name as name,
			    gender,
			    image_url AS imageUrl,
			    popularity_score AS popularityScore,
			    awards_count AS awardsCount,
			    role,
			    language,
			    l.name AS languageName,
			    mvr.name as roleName
			FROM
			    movie_person p
			LEFT JOIN
			    languages l ON p.language = l.id
			LEFT JOIN
			     movie_person_role mvr on p.role =  mvr.id   
											  
				  WHERE
						    (
						        :search IS NULL OR (
						            LOWER(l.name) LIKE LOWER(CONCAT('%', :search, '%'))
						           )
						        )
						    

						  AND (:language = 0 OR l.id = :language)

						 """, nativeQuery = true)
	Page<ActorDTO> getfillerActorWithLanguage(@Param("language") int language,
			@Param("search") String searchText, Pageable pageable);

}
