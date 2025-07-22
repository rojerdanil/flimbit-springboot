package com.riseup.flimbit.controllers;

import com.riseup.flimbit.entity.Announcement;
import com.riseup.flimbit.entity.dto.AnnouncementTableDTO;
import com.riseup.flimbit.request.AnnouncementRequest;
import com.riseup.flimbit.service.AnnouncementService;
import com.riseup.flimbit.utility.HttpResponseUtility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/announcements/")
public class AnnouncementController {

	@Autowired
    AnnouncementService service;

    @PostMapping("/add")
    public ResponseEntity<?> create(@RequestBody AnnouncementRequest request) {
        return HttpResponseUtility.getHttpSuccess(service.create(request));

    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?>  update(@PathVariable int id, @RequestBody AnnouncementRequest request) {
        return HttpResponseUtility.getHttpSuccess(service.update(id, request));
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        service.delete(id);
        return HttpResponseUtility.getHttpSuccess("deleted successfully");

    }

    @GetMapping("/annoncebyid/{id}")
    public ResponseEntity<?> getAnnonceById(@PathVariable int id) {
        return HttpResponseUtility.getHttpSuccess(service.getAnnonceById(id));

    }

    
    
    @GetMapping
    public List<Announcement> findAll() {
        return service.findAll();
    }
    
    @GetMapping("/dataTable")
    public ResponseEntity<?> getAllAnnouncements(
    		@RequestParam(value = "language", defaultValue = "0") int language,
    		@RequestParam(value = "status", required = false) String status,
    		@RequestParam(value = "searchText", required = false) String searchText,
    		@RequestParam(value = "start", defaultValue = "0") int start,
    		@RequestParam(value = "length", defaultValue = "10") int length,
    		@RequestParam(value = "sortColumn", defaultValue = "created_at") String sortColumn,
    		@RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder,
    		@RequestParam(value = "draw", defaultValue = "1") int draw
    ) {
    	
    	
    	int languagex = language;
    	status = status == null || status.isEmpty() ? null : status;
    	searchText = searchText == null || searchText.isEmpty() ? null : searchText;
    	
    	System.out.println(languagex + " "+ status + " "+searchText + draw  + " " + start + " "+length + " "+ sortOrder);
       
        Page<AnnouncementTableDTO> page = service.getFilteredAnnouncements(
        		languagex, status, searchText, start, length, sortColumn, sortOrder
        );

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", page.getTotalElements());
        response.put("recordsFiltered", page.getTotalElements());
        	response.put("data", page.getContent());
        return HttpResponseUtility.getHttpSuccess(response);

    }
}
