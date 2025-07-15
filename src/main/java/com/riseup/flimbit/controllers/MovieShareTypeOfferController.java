package com.riseup.flimbit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.OfferShareTypeMovie;
import com.riseup.flimbit.request.MovieShareOfferRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.OfferShareTypeMovieService;
import com.riseup.flimbit.utility.HttpResponseUtility;
import com.riseup.flimbit.utility.JwtService;

import java.util.List;

@RestController
@RequestMapping("/offer-mappings")
public class MovieShareTypeOfferController {
	
	@Autowired
	JwtService jwtService;
	@Value("${isValidateTokenEnable}")
    boolean isValidateTokenEnable;

    @Autowired
    private OfferShareTypeMovieService service;

    @PostMapping(value = "/saveMovieShareOffer")
    public ResponseEntity<?> create(
    		@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody MovieShareOfferRequest movieShareOfferRequest) {
        
    	
    	if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
	
    	return HttpResponseUtility.getHttpSuccess(service.save(movieShareOfferRequest));
    	
    	
    }
    @PostMapping(value = "/update/{id}")
    public ResponseEntity<?> update(
    		@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody MovieShareOfferRequest movieShareOfferRequest,
    		@PathVariable Long id) {
        
    	
    	if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
	
    	return HttpResponseUtility.getHttpSuccess(service.update(id,movieShareOfferRequest));
    	
    	
    }

    @GetMapping
    public List<OfferShareTypeMovie> getAll() {
        return service.getAll();
    }

    @GetMapping("/readShareOffer/{id}")
    public ResponseEntity<?> getById(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@PathVariable Long id) {
        
    	if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
	
    	
    	return HttpResponseUtility.getHttpSuccess(service.getById(id));
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> delete(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@PathVariable Long id) {
    	if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}

        service.delete(id);
        return HttpResponseUtility.getHttpSuccess("deleted successfully");

    }
    
}
