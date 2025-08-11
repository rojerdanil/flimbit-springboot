package com.riseup.flimbit.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.entity.dto.PayoutDTO;
import com.riseup.flimbit.entity.dto.UserPayoutInitiationDTO;
import com.riseup.flimbit.service.UserPayoutInitiationService;
import com.riseup.flimbit.utility.HttpResponseUtility;

@RestController
@RequestMapping(value = "/payoutInitiation")
public class PayoutInitiationController {
	@Autowired
	UserPayoutInitiationService payoutInitService;
	
	@GetMapping("/dataTableUserPayoutInitiation")
	public ResponseEntity<?> getDataTableUserPayout(
			@RequestParam int draw, 
			@RequestParam int start,
			@RequestParam int length, @RequestParam(required = false) String searchText,
			@RequestParam(defaultValue = "id") String sortColumn,
			@RequestParam(defaultValue = "asc") String sortOrder,
			@RequestParam(required = false) String language, 
			@RequestParam(required = false) String movie,
			@RequestParam(required = false) String status) {
		
		int languagex = language == null || language.isEmpty() ? 0 : Integer.parseInt(language);
		int moviex = movie == null || movie.isEmpty() ? 0 : Integer.parseInt(movie);
		status = status == null || status.isEmpty() ? null : status;
		searchText = searchText == null || searchText.isEmpty() ? null : searchText;
		Page<UserPayoutInitiationDTO> page = payoutInitService.gePayoutInitiationForDataTable(languagex, moviex, status, searchText,
				start, length, sortColumn, sortOrder);
		Map<String, Object> response = new HashMap<>();
		response.put("draw", draw);
		response.put("recordsTotal", page.getTotalElements());
		response.put("recordsFiltered", page.getTotalElements());
		response.put("data", page.getContent());
		return HttpResponseUtility.getHttpSuccess(response);
		
		
		
		
	}
	
	
	@GetMapping("/readPayoutInitiationByUserIdAndMovieId/{userId}/{movId}")
	public ResponseEntity<?> getInvestsharebyShareId(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken, @PathVariable("userId") int userId,
			@PathVariable("movId") int movId

	)

	{
		 System.out.println("clicked ");

		return HttpResponseUtility.getHttpSuccess(payoutInitService.getPayoutInitiationForUserIdAndMovieId(userId, movId));
	}

}
