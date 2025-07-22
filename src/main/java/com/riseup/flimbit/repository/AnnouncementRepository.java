package com.riseup.flimbit.repository;


import com.riseup.flimbit.entity.Announcement;
import com.riseup.flimbit.entity.dto.AnnouncementTableDTO;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
	

    @Query(value = """
        SELECT a.id, a.title, a.status, a.language_id AS languageId, 
             l.name AS languageName, a.start_datetime AS startTime, a.created_at AS createdAt,
             a.valid_until as validUntil,a.image_url as imageUrl,a.link_url as linkUrl,
             a.priority as priority , a.updated_at as updateDate,a.message as message,
             a.notify_email  as isNotifyEmail,
             a.notify_push  as isNotifyPush,
             a.notify_sms  as isNotifySms,
             a.notify_in_app  as isNotifyInApp
      FROM announcements a
      LEFT JOIN languages l ON l.id = a.language_id
      WHERE (:search IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:status IS NULL OR LOWER(a.status) = LOWER(:status))
        AND (:language = 0 OR a.language_id = :language)
       
        """, nativeQuery = true)
    Page<AnnouncementTableDTO> fetchAnnouncementsNative(
        @Param("language") int language,
        @Param("status") String status,
        @Param("search") String search
        , Pageable pageable
    );
    
    @Query(value = """
            SELECT COUNT(*) FROM announcement
            WHERE (:language IS NULL OR language_id = :language OR :language = '0')
              AND (:status IS NULL OR status = :status)
              AND (:search IS NULL OR LOWER(title) LIKE LOWER(CONCAT('%', :search, '%')))
            """, nativeQuery = true)
        long countFilteredAnnouncements(
            @Param("language") String language,
            @Param("status") String status,
            @Param("search") String search
        );
}
