package com.riseup.flimbit.controllers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.riseup.flimbit.entity.PromotionReward;
import com.riseup.flimbit.entity.dto.UserInvestmentSectionDTO;
import com.riseup.flimbit.request.MovieInvestRequest;
import com.riseup.flimbit.request.MovieRequest;
import com.riseup.flimbit.request.PayAllShareReturnRequest;
import com.riseup.flimbit.request.StatusRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.MovieInvestService;
import com.riseup.flimbit.utility.HttpResponseUtility;
import com.riseup.flimbit.utility.JwtService;

@RestController
@RequestMapping(value = "/movieInvest")
public class MovieInvestController {
	Logger logger = LoggerFactory.getLogger(MovieController.class);

	@Autowired
	MovieInvestService movieInvestService;

	@Value("${isValidateTokenEnable}")
	boolean isValidateTokenEnable;

	@Autowired
	JwtService jwtService;

	@PostMapping("/buyShare")
	public ResponseEntity<?> updateMovie(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken, @RequestBody MovieInvestRequest movieInvestdReq) {
		return ResponseEntity.status(HttpStatus.OK).body(movieInvestService.getBuyShares(movieInvestdReq, phoneNumber));
	}

	@GetMapping("/dataTableUserInvestment")
	public ResponseEntity<?> getPaginatedRewards(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken, @RequestParam int draw, @RequestParam int start,
			@RequestParam int length, @RequestParam(required = false) String searchText,
			@RequestParam(defaultValue = "id") String sortColumn, @RequestParam(defaultValue = "asc") String sortOrder,
			@RequestParam(required = false) String language, @RequestParam(required = false) String movie,
			@RequestParam(required = false) String status) {

		int languagex = language == null || language.isEmpty() ? 0 : Integer.parseInt(language);
		int moviex = movie == null || movie.isEmpty() ? 0 : Integer.parseInt(movie);
		status = status == null || status.isEmpty() ? null : status;
		searchText = searchText == null || searchText.isEmpty() ? null : searchText;

		Page<UserInvestmentSectionDTO> page = movieInvestService.getMovieInvestForUserInvestSection(languagex, moviex,
				status, searchText, start, length, sortColumn, sortOrder);
		Map<String, Object> response = new HashMap<>();
		response.put("draw", draw);
		response.put("recordsTotal", page.getTotalElements());
		response.put("recordsFiltered", page.getTotalElements());
		response.put("data", page.getContent());
		return HttpResponseUtility.getHttpSuccess(response);
	}

	@GetMapping("/readInvestbymovie/{id}/{userId}")
	public ResponseEntity<?> getInvestsharebymovId(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken, @PathVariable("id") int id,
			@PathVariable("userId") int userId)

	{

		return HttpResponseUtility.getHttpSuccess(movieInvestService.readInvestmentWithShareTypeByMovId(id, userId));
	}

	@GetMapping("/readInvestbymoviebyshareId/{id}/{userId}/{shareId}")
	public ResponseEntity<?> getInvestsharebyShareId(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken, @PathVariable("id") int id,
			@PathVariable("userId") int userId, @PathVariable("shareId") int shareId

	)

	{

		return HttpResponseUtility
				.getHttpSuccess(movieInvestService.getInvestmentsForMovIdAndUserIdAndShareTypeId(id, userId, shareId));
	}

	@PostMapping("/udpateStatus")
	public ResponseEntity<?> getPaginatedRewards(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken, @RequestBody StatusRequest statusRequest)

	{

		return HttpResponseUtility.getHttpSuccess(movieInvestService.updateInvestmentStatus(statusRequest));
	}

	@PostMapping("/repayAllInvestedforShare")
	public ResponseEntity<?> getRepayAllInvestedforShare(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken,
			@RequestBody PayAllShareReturnRequest statusRequest)

	{

		return HttpResponseUtility.getHttpSuccess(movieInvestService.repayShareInvestMoneyToUser(statusRequest));
	}
}
