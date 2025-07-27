package com.riseup.flimbit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.PromoCode;
import com.riseup.flimbit.entity.PromotionReward;
import com.riseup.flimbit.request.PromoCodeRequest;
import com.riseup.flimbit.request.PromoRewardMapRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.PromoCodeService;
import com.riseup.flimbit.utility.HttpResponseUtility;
import com.riseup.flimbit.utility.JwtService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/promo-codes")
public class PromoCodeController {

	@Autowired
	private PromoCodeService promoCodeService;

	@PostMapping("/create")
	public ResponseEntity<?> create(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken, @RequestBody PromoRewardMapRequest promoCode) {

		return HttpResponseUtility.getHttpSuccess(promoCodeService.savePromoCodeRewards(promoCode));

	}

	@GetMapping
	public List<PromoCode> getAll() {
		return promoCodeService.getAll();
	}

	@GetMapping("/{id}")
	public PromoCode getById(@PathVariable Long id) {
		return promoCodeService.getById(id);
	}

	@GetMapping("/code/{promoCode}")
	public PromoCode getByCode(@PathVariable String promoCode) {
		return promoCodeService.getByCode(promoCode)
				.orElseThrow(() -> new RuntimeException("Promo Code not found " + promoCode));
	}

	@GetMapping("/delete/{id}")
	public ResponseEntity<?> delete(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken, @PathVariable Long id) {
		promoCodeService.delete(id);
		return HttpResponseUtility.getHttpSuccess("deleted successfully");

	}

	@GetMapping("/page")
	public ResponseEntity<?> getPaginatedRewards(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken, @RequestParam int draw, @RequestParam int start,
			@RequestParam int length, @RequestParam(required = false) String searchText,
			@RequestParam(defaultValue = "id") String sortColumn,
			@RequestParam(defaultValue = "asc") String sortOrder) {

		Page<PromoCode> page = promoCodeService.getPaginated(start, length, searchText, sortColumn, sortOrder);
		Map<String, Object> response = new HashMap<>();
		response.put("draw", draw);
		response.put("recordsTotal", page.getTotalElements());
		response.put("recordsFiltered", page.getTotalElements());
		response.put("data", page.getContent());
		return HttpResponseUtility.getHttpSuccess(response);
	}

	@PostMapping("/createPromoCode")
	public ResponseEntity<?> createPromoCode(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken, @RequestBody PromoCodeRequest promoCode) {

		return HttpResponseUtility.getHttpSuccess(promoCodeService.savePromoCode(promoCode));

	}

	@PostMapping("/updatePromoCode")
	public ResponseEntity<?> updatePromoCode(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken, @RequestBody PromoCodeRequest promoCode) {

		return HttpResponseUtility.getHttpSuccess(promoCodeService.updatePromoCode(promoCode));

	}

}
