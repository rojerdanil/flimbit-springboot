package com.riseup.flimbit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.Offer;
import com.riseup.flimbit.entity.OfferType;
import com.riseup.flimbit.request.OfferRequest;
import com.riseup.flimbit.request.OfferTypeRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.OfferService;
import com.riseup.flimbit.utility.HttpResponseUtility;
import com.riseup.flimbit.utility.JwtService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/offers/")
public class OfferController {
	
	  @Autowired
		JwtService jwtService;
		@Value("${isValidateTokenEnable}")
	    boolean isValidateTokenEnable;
		
		 @Autowired
		    private OfferService offerService;
		
		 
	    @PostMapping("/paged")
	    public ResponseEntity<?> getPaginatedOfferTypes(
	    		@RequestHeader(value="deviceId") String deviceId,
	    		@RequestHeader(value="phoneNumber") String phoneNumber,
	    		@RequestHeader(value="accessToken") String accessToken,
	    		
	    		@RequestParam int draw,
	            @RequestParam int start,
	            @RequestParam int length,
	            @RequestParam(required = false) String searchText,
	            @RequestParam(defaultValue = "id") String sortColumn,
	            @RequestParam(defaultValue = "asc") String sortOrder
	    		) {
	    	
	    	
	    	if(isValidateTokenEnable)
			{	
			 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
	           if (commonToken.getStatus() != Messages.SUCCESS) {
		            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
		        }
			}
	    	
	        Page<Offer> offerPage = offerService.getPagedOfferTypes(start, length, searchText, sortColumn, sortOrder);

	        Map<String, Object> result = new HashMap<>();
	        result.put("draw", draw);
	        result.put("recordsTotal", offerPage.getTotalElements());
	        result.put("recordsFiltered", offerPage.getTotalElements());
	        result.put("data", offerPage.getContent());

	        return HttpResponseUtility.getHttpSuccess(result);
	    }

   

    @PostMapping(value = "/insert")
    public ResponseEntity<?> create(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody OfferRequest offerReq) {
    	
    	if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
    	
        return HttpResponseUtility.getHttpSuccess(offerService.create(offerReq));

    }

    @GetMapping(value = "/all")
    public ResponseEntity<?> getAll(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken) {
    
    	if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
    
    	
    	return HttpResponseUtility.getHttpSuccess(offerService.findAll());
    }

    @GetMapping("/offerId/{id}")
    public ResponseEntity<?> getById(
    		@RequestHeader(value="deviceId") String deviceId,
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
        		
    	
    	

        return HttpResponseUtility.getHttpSuccess(offerService.findById(id)) ;
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?>  update(@PathVariable long id, 
    		@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody OfferRequest offerReq) {
    	
    
    		if(isValidateTokenEnable)
    		{	
    		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
               if (commonToken.getStatus() != Messages.SUCCESS) {
    	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
    	        }
    		}
        	
    	
    	
        return HttpResponseUtility.getHttpSuccess(offerService.update(id, offerReq));
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> delete(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@PathVariable long id) {

		if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
    	
    	
    	
        return HttpResponseUtility.getHttpSuccess(offerService.delete(id));
    }
    @GetMapping("/movieOffer/{id}")
    public ResponseEntity<?> getMovieOffer(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@PathVariable long id) {

		if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
    	
    	
    	
        return HttpResponseUtility.getHttpSuccess(offerService.getMovieOffer(id));
    }
}
