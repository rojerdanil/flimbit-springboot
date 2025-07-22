package com.riseup.flimbit.serviceImp;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.entity.AuditLog;
import com.riseup.flimbit.entity.SystemSettings;
import com.riseup.flimbit.entity.dto.AnnouncementTableDTO;
import com.riseup.flimbit.repository.AuditLogRepository;
import com.riseup.flimbit.repository.SystemSettingsRepository;
import com.riseup.flimbit.response.AuditGroupResponse;
import com.riseup.flimbit.response.KeyValueResponse;
import com.riseup.flimbit.utility.DateUtility;
import com.riseup.flimbit.utility.JsonUtil;

@Service
public class AuditLogServiceImp {
	@Autowired
	AuditLogRepository auditLogRepository;
	
	@Autowired
	SystemSettingsRepository  systemRepository;
	
	public void logAction(int userId, String actionType, String entityName, 
			int entityId, String description, Object requestData) {
		
	 Optional<SystemSettings> sysSettingOPt   =	systemRepository.findByKeyIgnoreCaseAndGroupNameIgnoreCase(EntityName.AUDIT_LOG.name(), EntityName.AUDIT_LOG.name());
		
	 if (!sysSettingOPt.isPresent() || sysSettingOPt.get().getValue().equalsIgnoreCase("active")) {	 
	 
		  try {
			  
			  String json = (requestData != null) 
		                ? new ObjectMapper().writeValueAsString(requestData) 
		                : "{}";
       

        auditLogRepository.saveAudit(actionType,new Timestamp(System.currentTimeMillis())
        		,description,entityId,entityName,json,userId);
		  }
		  catch (JsonProcessingException e) {
		        throw new RuntimeException("Failed to serialize status request for audit", e);
		    }
    }
	 else
	 {
		//  generate Warning Mail to activate  audit
	 }
	
	}
	
	
	public Page<AuditLog> getFilteredAudit(String startDate,String endDate,String entity,String action ,String searchText, int start, int length,
			String sortColumn, String sortOrder) {
		// TODO Auto-generated method stub
		   
		
		    int page = start / length;
	        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
	        Pageable pageable = PageRequest.of(page, length, sort);
	     
		    
	       return  auditLogRepository.fetchAudtitDataTable(
	    		
	    		   entity,
	    		   action,
		        searchText,
		        startDate,
		        endDate,
		        
		        pageable
		    );

	    }
	
	public AuditGroupResponse getAuditGroup()
	{
		
		return  AuditGroupResponse.builder().entityGroup(auditLogRepository.getEntityGroup())
				.actionGroup(auditLogRepository.getActionGroup()).build();
	}
}
