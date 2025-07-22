package com.riseup.flimbit.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.MoviePerson;
import com.riseup.flimbit.entity.dto.ActorDTO;
import com.riseup.flimbit.entity.dto.PartnerDTO;
import com.riseup.flimbit.request.MoviePersonRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.MoviePersonService;
import com.riseup.flimbit.utility.CommonUtilty;
import com.riseup.flimbit.utility.HttpResponseUtility;
import com.riseup.flimbit.utility.JwtService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/movie-person")
public class ActorsController {

    @Autowired
    private MoviePersonService service;
    
   
	

    @PostMapping(value = "/add")
    public ResponseEntity<?> create(@RequestBody MoviePersonRequest person) {
    	
    	
        return HttpResponseUtility.getHttpSuccess(service.create(person));

        
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody MoviePersonRequest person) {
        return HttpResponseUtility.getHttpSuccess(service.update(id, person));
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        service.delete(id);
        return HttpResponseUtility.getHttpSuccess("deleted successfully");

    }

    @GetMapping
    public List<MoviePerson> findAll() {
        return service.findAll();
    }
    
    @GetMapping(value = "/read/{id}")
    public ResponseEntity<?> findActor(@PathVariable Integer id) {  
    	return HttpResponseUtility.getHttpSuccess(service.findByActorId(id));   
    
    }
    
    @GetMapping("/movie_by_languagae")
    public ResponseEntity<?> getMoviePersonBylanguage(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		 @RequestParam("id") int id) {
    	
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
        		.status(Messages.STATUS_SUCCESS)
        		.message("Data reads")
        		.result(service.findAllByLanguagId(id)).build());
    }
    
    @GetMapping("/dataTable")
    public ResponseEntity<?> getAllActors(
    		@RequestParam(value = "language", defaultValue = "0") int language,
    		@RequestParam(value = "searchText", required = false) String searchText,
    		@RequestParam(value = "start", defaultValue = "0") int start,
    		@RequestParam(value = "length", defaultValue = "10") int length,
    		@RequestParam(value = "sortColumn", defaultValue = "created_at") String sortColumn,
    		@RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder,
    		@RequestParam(value = "draw", defaultValue = "1") int draw
    ) {
    	
    	
    	int languagex = language ;
    	searchText = searchText == null || searchText.isEmpty() ? null : searchText;

    	//System.out.println(languagex + "rojer  "+   " "+searchText + draw  + " " + start + " "+length + " "+ sortOrder);
       
        Page<ActorDTO> page = service.getfillerActorWithLanguage(
        		languagex,  searchText, start, length, sortColumn, sortOrder
        );

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", page.getTotalElements());
        response.put("recordsFiltered", page.getTotalElements());
        	response.put("data", page.getContent());
        return HttpResponseUtility.getHttpSuccess(response);

    }

}
