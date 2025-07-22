package com.riseup.flimbit.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.entity.dto.AnnouncementTableDTO;
import com.riseup.flimbit.entity.dto.InfluencerDTO;
import com.riseup.flimbit.request.AnnouncementRequest;
import com.riseup.flimbit.request.InfluencerRequest;
import com.riseup.flimbit.service.InfluencerService;
import com.riseup.flimbit.utility.HttpResponseUtility;

@RestController
@RequestMapping("/influencers")
public class InfluencerController {
	
	@Autowired
	InfluencerService influenceService;
	
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
	       
	        Page<InfluencerDTO> page = influenceService.getInfluencerDataTable(
	        		languagex, status, searchText, start, length, sortColumn, sortOrder
	        );

	        Map<String, Object> response = new HashMap<>();
	        response.put("draw", draw);
	        response.put("recordsTotal", page.getTotalElements());
	        response.put("recordsFiltered", page.getTotalElements());
	        	response.put("data", page.getContent());
	        return HttpResponseUtility.getHttpSuccess(response);

	    }

	   @PostMapping("/add")
	    public ResponseEntity<?> create(@RequestBody InfluencerRequest request) {
	        return HttpResponseUtility.getHttpSuccess(influenceService.create(request));

	    }
	   @GetMapping("/influencerbyid/{id}")
	    public ResponseEntity<?> getAnnonceById(@PathVariable int id) {
	        return HttpResponseUtility.getHttpSuccess(influenceService.getInfluencerById(id));
	    }
	   @PostMapping("/update/{id}")
	    public ResponseEntity<?>  update(@PathVariable int id, @RequestBody InfluencerRequest request) {
	        return HttpResponseUtility.getHttpSuccess(influenceService.update(id, request));
	    }
	   
	    @GetMapping("/delete/{id}")
	    public ResponseEntity<?> delete(@PathVariable int id) {
	    	influenceService.delete(id);
	        return HttpResponseUtility.getHttpSuccess("deleted successfully");

	    }

}
