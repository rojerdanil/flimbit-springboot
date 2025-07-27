package com.riseup.flimbit.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.constant.RewardTarget;
import com.riseup.flimbit.constant.RewardType;
import com.riseup.flimbit.constant.StatusEnum;
import com.riseup.flimbit.entity.PromotionReward;
import com.riseup.flimbit.entity.PromotionType;
import com.riseup.flimbit.request.PromotionRewardRequest;
import com.riseup.flimbit.request.PromotionTypeRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.dto.KeyValueDtoResponse;
import com.riseup.flimbit.response.dto.RewardTypeAndTargetDtoRes;
import com.riseup.flimbit.service.PromotionRewardService;
import com.riseup.flimbit.utility.HttpResponseUtility;
import com.riseup.flimbit.utility.JwtService;


@RestController
@RequestMapping("/reward-types")

public class RewardController {
	
	

	@Autowired
	PromotionRewardService promotionRewardService;
	
	@GetMapping("/page")
	public ResponseEntity<?> getPaginatedRewards(
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
		
		
    	
	    Page<PromotionReward> page = promotionRewardService.getPaginated(start, length, searchText, sortColumn, sortOrder);
	    Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", page.getTotalElements());
        response.put("recordsFiltered", page.getTotalElements());
        response.put("data", page.getContent());
        return HttpResponseUtility.getHttpSuccess(response);
	}
	
	 @PostMapping(value = "/insert")
	    public ResponseEntity<?> create(
	    		@RequestHeader(value="deviceId") String deviceId,
	    		@RequestHeader(value="phoneNumber") String phoneNumber,
	    		@RequestHeader(value="accessToken") String accessToken,
	    		@RequestBody PromotionRewardRequest promotionRewardReq) {
	    	
	    	
	        return  HttpResponseUtility.getHttpSuccess(promotionRewardService.createReward(promotionRewardReq));
	    	
	    }

	 @GetMapping("/rewards/{id}")
	    public ResponseEntity<?> getPromotionTypeById(
	    		@RequestHeader(value="deviceId") String deviceId,
	    		@RequestHeader(value="phoneNumber") String phoneNumber,
	    		@RequestHeader(value="accessToken") String accessToken,
	    		@PathVariable Long id
	    		
	    		) {
	     
	        return HttpResponseUtility.getHttpSuccess(promotionRewardService.getRewardById(id));

	    }
	 
	 
	 @PostMapping(value = "/update/{id}")
	    public ResponseEntity<?> update(
	    		@RequestHeader(value="deviceId") String deviceId,
	    		@RequestHeader(value="phoneNumber") String phoneNumber,
	    		@RequestHeader(value="accessToken") String accessToken,
	    		@RequestBody PromotionRewardRequest promotionRewardReq,
	    		@PathVariable Long id) {
	    	
	        return  HttpResponseUtility.getHttpSuccess(promotionRewardService.updateReward(id,promotionRewardReq));
	    	
	    }

	 @GetMapping("/delete/{id}")
	    public ResponseEntity<?> delete(@RequestHeader(value="deviceId") String deviceId,
	    		@RequestHeader(value="phoneNumber") String phoneNumber,
	    		@RequestHeader(value="accessToken") String accessToken,
	    		@PathVariable Long id) {
			/*
			 * if(isValidateTokenEnable) { CommonResponse commonToken =
			 * jwtService.validateToken(accessToken, deviceId, phoneNumber); if
			 * (commonToken.getStatus() != Messages.SUCCESS) { return
			 * ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken); } }
			 */	    	promotionRewardService.deleteById(id);
	        return HttpResponseUtility.getHttpSuccess("deleted successfully");

	    }
	 
    	 @GetMapping("/rewardTypeAndTarget")
	    public ResponseEntity<?> delete(@RequestHeader(value="deviceId") String deviceId,
	    		@RequestHeader(value="phoneNumber") String phoneNumber,
	    		@RequestHeader(value="accessToken") String accessToken) {
	   	
	    	List<KeyValueDtoResponse> types = Arrays.stream(RewardType.values())
	                .map(rt -> new KeyValueDtoResponse(rt.name(), rt.getLabel()))
	                .collect(Collectors.toList());
	    	
	    	List<KeyValueDtoResponse> target = Arrays.stream(RewardTarget.values())
	                .map(rt -> new KeyValueDtoResponse(rt.name(), rt.getLabel()))
	                .collect(Collectors.toList());
	    	
	    	RewardTypeAndTargetDtoRes rewardTypeTarget = RewardTypeAndTargetDtoRes.builder()
	    			 .types(types).target(target).build();
	    	
	        return HttpResponseUtility.getHttpSuccess(rewardTypeTarget);

	    }
	 

    	 
    	 @GetMapping("/rewardsbytype/{id}")
 	    public ResponseEntity<?> getRewardsByrewardTypeId(
 	    		@RequestHeader(value="deviceId") String deviceId,
 	    		@RequestHeader(value="phoneNumber") String phoneNumber,
 	    		@RequestHeader(value="accessToken") String accessToken,
 	    		@PathVariable Long id) {
    		 
    	 	        return HttpResponseUtility.getHttpSuccess(promotionRewardService.getRewardsByPromotionTypeAndStatus(id,StatusEnum.ACTIVE.getDescription()));

 	    }
}
