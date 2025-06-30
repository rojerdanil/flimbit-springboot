package com.riseup.flimbit.controllers;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.MovieActor;
import com.riseup.flimbit.request.MovieActorRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.MovieActorService;
import com.riseup.flimbit.utility.JwtService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/actors-in-movie")
public class MovieActorPlayingController {
	Logger logger
    = LoggerFactory.getLogger(MovieActorPlayingController.class);
	
	@Autowired
	JwtService jwtService;
	@Value("${isValidateTokenEnable}")
    boolean isValidateTokenEnable;
	
	@Autowired
	private MovieActorService service;

	@PostMapping(value = "/addMovieActor")
	public ResponseEntity<?> create(
			@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody MovieActorRequest actor) {
	
		
		
		 
				if(isValidateTokenEnable)
				{	
				 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
		           if (commonToken.getStatus() != Messages.SUCCESS) {
			            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
			        }
				}
				
		        return ResponseEntity.status(HttpStatus.OK).body(service.create(actor));
		
	}

	@PutMapping("/{id}")
	public MovieActor update(@PathVariable Integer id, @RequestBody MovieActor actor) {
		return service.update(id, actor);
	}

	@GetMapping("/deleteMoviePlay")
	public ResponseEntity<?> delete(@RequestHeader(value="deviceId") String deviceId,
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
		service.delete(id);
		CommonResponse commRes = CommonResponse.builder().status(Messages.STATUS_SUCCESS)
				.build();
		return ResponseEntity.status(HttpStatus.OK).body(commRes);
		
	}

	@GetMapping(value="/actorsInMovieById")
	public ResponseEntity<?> getAll(	@RequestHeader(value="deviceId") String deviceId,
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
		return ResponseEntity.status(HttpStatus.OK).body(service.getAllRoleByMovieId(id));
	}
}
