package com.riseup.flimbit.controllers;


import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.PromotionType;
import com.riseup.flimbit.repository.PromotionTypeRepository;
import com.riseup.flimbit.request.PromotionTypeRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.PromotionTypeService;
import com.riseup.flimbit.utility.CommonUtilty;
import com.riseup.flimbit.utility.HttpResponseUtility;
import com.riseup.flimbit.utility.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/promotion-types")
public class PromotionTypeController {

    @Autowired
    private PromotionTypeService promotionTypeService;
    
    @Autowired
    PromotionTypeRepository promotionTypeRepository;

    @Autowired
	JwtService jwtService;
	@Value("${isValidateTokenEnable}")
    boolean isValidateTokenEnable;
    
    
    @GetMapping("/page")
    public ResponseEntity<?> getPaginatedPromotionTypes(
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
    	
    	
        Page<PromotionType> page = promotionTypeService.getPaginated(
                start, length, searchText, sortColumn, sortOrder
        );

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", page.getTotalElements());
        response.put("recordsFiltered", page.getTotalElements());
        response.put("data", page.getContent());
        return HttpResponseUtility.getHttpSuccess(response);
    }

    // Optional: Add CRUD endpoints if needed
    @PostMapping(value = "/insertUpdate")
    public ResponseEntity<?> create(
    		@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody PromotionTypeRequest promotionTypeReq) {
    	if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
    	
    	
        return  ResponseEntity.status(HttpStatus.OK).body(
            	CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_SUCCESS).result(	
            			promotionTypeService.save(promotionTypeReq)).build());

    	
    }
    @GetMapping("/promotionType/{id}")
    public ResponseEntity<?> getPromotionTypeById(@PathVariable Long id) {
        PromotionType proType =  promotionTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PromoType is not found for id :" +id));
        
        ;
        return HttpResponseUtility.getHttpSuccess(proType);

    }

    //@PutMapping("/{id}")
    //public PromotionType update(@PathVariable Long id, @RequestBody PromotionType updatedType) {
     //   updatedType.setId(id);
       // return promotionTypeService.save(updatedType);
    //}

    @GetMapping("/deleteBromoType/{id}")
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
        return HttpResponseUtility.getHttpSuccess(promotionTypeService.deleteById(id));

    }
    @GetMapping("/findAll")
    public ResponseEntity<?> getAllRecords(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken) {
    	if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
        return HttpResponseUtility.getHttpSuccess(promotionTypeService.findAllRecords());

    }
}
