package com.riseup.flimbit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
	
	
	
}