package com.riseup.flimbit.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.request.DeleteRequest;
import com.riseup.flimbit.request.MovieRequest;
import com.riseup.flimbit.request.MovieSearchRequest;
import com.riseup.flimbit.request.UserRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.MovieService;
import com.riseup.flimbit.utility.JwtService;

import jakarta.persistence.NamedStoredProcedureQueries;

@RestController
@RequestMapping(value = "/movie")
public class MovieController {
	Logger logger
    = LoggerFactory.getLogger(MovieController.class);
	
	@Autowired
	JwtService jwtService;
	@Value("${isValidateTokenEnable}")
    boolean isValidateTokenEnable;
	
	@Autowired
	MovieService movieService;
	
	
	@PostMapping("/listMovies")
    public ResponseEntity<?> listMovies(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody MovieSearchRequest movieSearchRequest
    		)
    {
		if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
		
        return ResponseEntity.status(HttpStatus.OK).body(movieService.getMoviesByLanguage(movieSearchRequest));
    }
	
	
	@PostMapping("/updateMovie")
    public ResponseEntity<?> updateMovie(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody MovieRequest movieRequest)
    {
		if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(movieService.updateMovie(movieRequest));
    }
	
	@PostMapping("/addMovie")
    public ResponseEntity<?> addMovie(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody MovieRequest movieRequest)
    {
		if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
        return ResponseEntity.status(HttpStatus.OK).body(movieService.addMovie(movieRequest));

    }
	@PostMapping("/deletMovie")
    public ResponseEntity<?> deletMovie(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody DeleteRequest movieIds)
    {
		if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
        return ResponseEntity.status(HttpStatus.OK).body(movieService.deleteMovie(movieIds.getIdsList()));
    }


}
