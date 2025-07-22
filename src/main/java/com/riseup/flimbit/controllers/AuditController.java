package com.riseup.flimbit.controllers;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.entity.AuditLog;
import com.riseup.flimbit.entity.dto.AnnouncementTableDTO;
import com.riseup.flimbit.serviceImp.AuditLogServiceImp;
import com.riseup.flimbit.utility.DateUtility;
import com.riseup.flimbit.utility.HttpResponseUtility;

@RestController
@RequestMapping("/audit/")
public class AuditController {
	
	@Autowired
	AuditLogServiceImp  auditLogService;
	
	
	@GetMapping("/dataTable")
    public ResponseEntity<?> getAllAnnouncements(
    		@RequestParam(value = "startDate", required = false) String startDate,
    		@RequestParam(value = "endDate", required = false) String endDate,
    		@RequestParam(value = "entity", required = false) String entity,
    		@RequestParam(value = "action", required = false) String action,
    		@RequestParam(value = "searchText", required = false) String searchText,
    		@RequestParam(value = "start", defaultValue = "0") int start,
    		@RequestParam(value = "length", defaultValue = "10") int length,
    		@RequestParam(value = "sortColumn", defaultValue = "created_at") String sortColumn,
    		@RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder,
    		@RequestParam(value = "draw", defaultValue = "1") int draw
    ) {
    	
    	
		entity = entity == null || entity.isEmpty() ? null : entity;
		action = action == null || action.isEmpty() ? null : action;

    	searchText = searchText == null || searchText.isEmpty() ? null : searchText;
    	
    	   
	       String startDateDate = startDate ==  null || startDate.isEmpty() ?  null :startDate;
	       String endDateDate = endDate ==  null || endDate.isEmpty() ?  null : endDate;

    	
    	System.out.println(entity + " "+ action + " "+searchText + startDateDate  + " " + endDateDate + " "+length + " "+ sortOrder);
       
        Page<AuditLog> page = auditLogService.getFilteredAudit(startDateDate,endDateDate,entity,action, searchText, start, length, sortColumn, sortOrder);

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", page.getTotalElements());
        response.put("recordsFiltered", page.getTotalElements());
        	response.put("data", page.getContent());
        return HttpResponseUtility.getHttpSuccess(response);

    }
	 @GetMapping("/readGroupEntity")
	    public ResponseEntity<?> getGroup() {
	        return HttpResponseUtility.getHttpSuccess(auditLogService.getAuditGroup());
	    }


}
