package com.riseup.flimbit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.Influencer;
import com.riseup.flimbit.entity.dto.AnnouncementTableDTO;
import com.riseup.flimbit.entity.dto.InfluencerDTO;

@Repository
public interface InfluencerRepository extends JpaRepository<Influencer, Integer> {
	// Custom query methods can be added here if needed

	@Query(value = """
			    select inf.id  as id,
				inf.first_name as firstName,
				inf.last_name as lastName,
				inf.email as email,
				inf.phone_number as phoneNumber,
				inf.social_media_handle  as socialMediaHandle,
				inf.created_at as createdAt,
				inf.updated_at as updatedAt,
				pc.promo_code as promoCode,
				l.name AS languageName,
				inf.status as status
				from influencers as inf
				left join promo_codes pc on pc.promo_code = inf.promo_code
	            LEFT JOIN languages l ON l.id = inf.language_id
			WHERE (:search IS NULL OR LOWER(inf.first_name) LIKE LOWER(CONCAT('%', :search, '%'))
			OR LOWER(inf.last_name) LIKE LOWER(CONCAT('%', :search, '%'))
			OR LOWER(inf.promo_code) LIKE LOWER(CONCAT('%', :search, '%'))
			)
			AND (:status IS NULL OR LOWER(inf.status) = LOWER(:status))
			AND (:language = 0 OR inf.language_id = :language)
			ORDER BY  inf.updated_at desc

					        """, nativeQuery = true)
	Page<InfluencerDTO> getDataTableInfluencer(@Param("language") int language, @Param("status") String status,
			@Param("search") String search, Pageable pageable);
}
