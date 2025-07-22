package com.riseup.flimbit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.Partner;
import com.riseup.flimbit.entity.dto.AnnouncementTableDTO;
import com.riseup.flimbit.entity.dto.PartnerDTO;

@Repository
public interface PartnerRepository extends JpaRepository<Partner, Integer> {
	
	
	 @Query(value = """
		        SELECT  a.*, l.name as languageName
		      FROM partner a
		      LEFT JOIN languages l ON l.id = a.language_id
		      WHERE (:search IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%'))
		      OR LOWER(a.phone) LIKE LOWER(CONCAT('%', :search, '%'))
		      )
		        AND (:status IS NULL OR LOWER(a.status) = LOWER(:status))
		        AND (:language = 0 OR a.language_id = :language)
		        AND (:type IS NULL OR LOWER(a.type) = LOWER(:type))

		        """, nativeQuery = true)
		    Page<PartnerDTO> getFilterPartnerDataTable(
		        @Param("language") int language,
		        @Param("status") String status,
		        @Param("search") String search
		        , Pageable pageable
		        ,String type
		    );
}
