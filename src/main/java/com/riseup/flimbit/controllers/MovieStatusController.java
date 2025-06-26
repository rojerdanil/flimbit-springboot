package com.riseup.flimbit.controllers;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.MovieStatus;
import com.riseup.flimbit.request.MovieSearchRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.MovieStatusService;
import com.riseup.flimbit.utility.JwtService;

import java.util.List;

@RestController
@RequestMapping("/movie-status")
public class MovieStatusController {
	@Autowired
	JwtService jwtService;
	@Value("${isValidateTokenEnable}")
    boolean isValidateTokenEnable;

    @Autowired
    private MovieStatusService service;

    @PostMapping
    public MovieStatus create(@RequestBody MovieStatus status) {
        return service.create(status);
    }

    @PutMapping("/{id}")
    public MovieStatus update(@PathVariable Integer id, @RequestBody MovieStatus status) {
        return service.update(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping("/allstatus")
    public ResponseEntity<?> findAll(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken
    		) {
    	
    	if(isValidateTokenEnable)
		{	
    		CommonResponse	 commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder().status(Messages.STATUS_SUCCESS).result(service.findAll()).build());

         
    }
}
