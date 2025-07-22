package com.riseup.flimbit.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.AuditLog;
import com.riseup.flimbit.entity.dto.AnnouncementTableDTO;
import com.riseup.flimbit.response.KeyValueResponse;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
	
	
	@Modifying
	@Query(value = """
	    INSERT INTO audit_log 
	    (action_type, created_at, description, entity_id, entity_name, request_data, user_id)
	    VALUES (?1, ?2, ?3, ?4, ?5, CAST(?6 AS jsonb), ?7)
	""", nativeQuery = true)
	void saveAudit(
	    String actionType,
	    Timestamp createdAt,
	    String description,
	    int entityId,
	    String entityName,
	    String requestData,  // This is JSON string
	    int userId
	);
	
	
	@Query(value = """
		    SELECT a.*
		    FROM audit_log a
		    WHERE (:search IS NULL OR LOWER(a.action_type) LIKE LOWER(CONCAT('%', :search, '%')))
		      AND (:actionType IS NULL OR LOWER(a.action_type) = LOWER(:actionType))
		      AND (:entity IS NULL OR LOWER(a.entity_name) = LOWER(:entity))
		      AND (
		          (:fromDate IS NULL OR :toDate IS NULL)
		          OR (a.created_at BETWEEN  CAST(:fromDate AS TIMESTAMP) AND CAST(:toDate AS TIMESTAMP))
		      )
		    """, nativeQuery = true)
		Page<AuditLog> fetchAudtitDataTable(
		    @Param("entity") String entity,    
		    @Param("actionType") String actType,
		    @Param("search") String search,
		    @Param("fromDate") String fromDate,
		    @Param("toDate") String toDate,
		    Pageable pageable
		);


	 @Query(value = "SELECT u.entity_name as key , u.entity_name as value FROM audit_log u GROUP BY u.entity_name", nativeQuery = true)
	    List<KeyValueResponse> getEntityGroup();
	 @Query(value = "SELECT u.action_type as key , u.action_type as value FROM audit_log u GROUP BY u.action_type", nativeQuery = true)
	    List<KeyValueResponse> getActionGroup();
	 
	
	
}