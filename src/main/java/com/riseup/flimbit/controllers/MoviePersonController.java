package com.riseup.flimbit.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.MoviePerson;
import com.riseup.flimbit.request.MoviePersonRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.MoviePersonService;
import com.riseup.flimbit.utility.CommonUtilty;
import com.riseup.flimbit.utility.JwtService;

import java.util.List;

@RestController
@RequestMapping("/movie-person")
public class MoviePersonController {

    @Autowired
    private MoviePersonService service;
    
    @Autowired
	JwtService jwtService;
	@Value("${isValidateTokenEnable}")
    boolean isValidateTokenEnable;
	

    @PostMapping(value = "/addMoviePerson")
    public ResponseEntity<?> create(@RequestBody MoviePersonRequest person) {
    	
    	
        return ResponseEntity.status(HttpStatus.OK).body(service.create(person));

        
    }

    @PutMapping("/{id}")
    public MoviePerson update(@PathVariable Integer id, @RequestBody MoviePerson person) {
        return service.update(id, person);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping
    public List<MoviePerson> findAll() {
        return service.findAll();
    }
    
    @GetMapping("/movie_by_languagae")
    public ResponseEntity<?> getMoviePersonBylanguage(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		 @RequestParam("id") int id) {
    	if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
        		.status(Messages.STATUS_SUCCESS)
        		.message("Data reads")
        		.result(service.findAllByLanguagId(id)).build());
    }
}
