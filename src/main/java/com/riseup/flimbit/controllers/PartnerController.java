package com.riseup.flimbit.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.entity.Partner;
import com.riseup.flimbit.entity.dto.AnnouncementTableDTO;
import com.riseup.flimbit.entity.dto.PartnerDTO;
import com.riseup.flimbit.request.PartnerRequest;
import com.riseup.flimbit.service.PartnerService;
import com.riseup.flimbit.utility.HttpResponseUtility;

@RestController
@RequestMapping("/partners")
public class PartnerController {

    @Autowired
    private PartnerService partnerService;

    @PostMapping("/add")
    public ResponseEntity<?> addPartner(@RequestBody PartnerRequest request) {
        
        return HttpResponseUtility.getHttpSuccess(partnerService.addPartner(request));

    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updatePartner(@PathVariable int id, @RequestBody PartnerRequest request) {
    	
        return HttpResponseUtility.getHttpSuccess(partnerService.updatePartner(id, request)); 
    }
    
    @GetMapping("/dataTable")
    public ResponseEntity<?> getAllAnnouncements(
    		@RequestParam(value = "language", defaultValue = "0") int language,
    		@RequestParam(value = "status", required = false) String status,
    		@RequestParam(value = "type", required = false) String type,
    		@RequestParam(value = "searchText", required = false) String searchText,
    		@RequestParam(value = "start", defaultValue = "0") int start,
    		@RequestParam(value = "length", defaultValue = "10") int length,
    		@RequestParam(value = "sortColumn", defaultValue = "created_at") String sortColumn,
    		@RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder,
    		@RequestParam(value = "draw", defaultValue = "1") int draw
    ) {
    	
    	
    	int languagex = language ;
    	status = status == null || status.isEmpty() ? null : status;
    	searchText = searchText == null || searchText.isEmpty() ? null : searchText;
    	type = type == null || type.isEmpty() ? null : type;

    	System.out.println(languagex + "rojer  "+ status + " "+searchText + draw  + " " + start + " "+length + " "+ sortOrder);
       
        Page<PartnerDTO> page = partnerService.getFilteredPartners(
        		languagex, status, searchText, start, length, sortColumn, sortOrder,type
        );

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", page.getTotalElements());
        response.put("recordsFiltered", page.getTotalElements());
        	response.put("data", page.getContent());
        return HttpResponseUtility.getHttpSuccess(response);

    }


    @GetMapping
    public ResponseEntity<List<Partner>> getAllPartners() {
        return ResponseEntity.ok(partnerService.getAllPartners());
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<?> getPartnerById(@PathVariable int id) {
    	
        return HttpResponseUtility.getHttpSuccess(partnerService.getPartnerById(id));
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deletePartner(@PathVariable int id) {
        partnerService.deletePartner(id);
        return HttpResponseUtility.getHttpSuccess("deleted successfully");
    }
}
