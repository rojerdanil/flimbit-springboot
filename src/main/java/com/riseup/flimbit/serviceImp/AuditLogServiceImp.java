package com.riseup.flimbit.serviceImp;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.AuditLog;
import com.riseup.flimbit.repository.AuditLogRepository;
import com.riseup.flimbit.utility.JsonUtil;

@Service
public class AuditLogServiceImp {
	@Autowired
	AuditLogRepository auditLogRepository;
	
	public void logAction(Long userId, String actionType, String entityName, 
			Long entityId, String description, Object requestData) {
        AuditLog log = AuditLog.builder()
            .userId(userId)
            .actionType(actionType)
            .entityName(entityName)
            .entityId(entityId)
            .description(description)
            .requestData(requestData != null ? JsonUtil.toJson(requestData) : null)
            .createdAt(new Timestamp(System.currentTimeMillis()))
            .build();

        auditLogRepository.save(log);
    }
	

}
