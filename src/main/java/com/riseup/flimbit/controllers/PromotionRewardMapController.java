package com.riseup.flimbit.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.PromotionRewardsMap;
import com.riseup.flimbit.entity.dto.PromotionRewardMapDTO;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.PromotionRewardMapService;
import com.riseup.flimbit.utility.HttpResponseUtility;
import com.riseup.flimbit.utility.JwtService;

@RestController
@RequestMapping("/promotion-rewards-map")
public class PromotionRewardMapController {
	
	@Autowired
	JwtService jwtService;
	@Value("${isValidateTokenEnable}")
    boolean isValidateTokenEnable;
	
	@Autowired
	PromotionRewardMapService promoRewardMapService;
	
	  
	@GetMapping("/page/{promoId}")
    public ResponseEntity<?> getPaginatedRewards(
            @RequestHeader(value = "deviceId") String deviceId,
            @RequestHeader(value = "phoneNumber") String phoneNumber,
            @RequestHeader(value = "accessToken") String accessToken,
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam(required = false) String searchText,
            @RequestParam(defaultValue = "id") String sortColumn,
            @RequestParam(defaultValue = "asc") String sortOrder,
    		@PathVariable Long promoId

    ) {
        if (isValidateTokenEnable) {
            CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
            if (commonToken.getStatus() != Messages.SUCCESS) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
            }
        }

        Page<PromotionRewardMapDTO> page = promoRewardMapService.getPaginated(promoId,start, length, searchText, sortColumn, sortOrder);

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", page.getTotalElements());
        response.put("recordsFiltered", page.getTotalElements());
        response.put("data", page.getContent());

        return HttpResponseUtility.getHttpSuccess(response);
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
	    	promoRewardMapService.deleteById(id);
	        return HttpResponseUtility.getHttpSuccess("deleted successfully");

	    }

}
