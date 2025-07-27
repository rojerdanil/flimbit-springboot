package com.riseup.flimbit.controllers;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.OfferType;
import com.riseup.flimbit.request.OfferTypeRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.OfferTypeService;
import com.riseup.flimbit.utility.HttpResponseUtility;
import com.riseup.flimbit.utility.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/offertypes")
public class OfferTypeController {

	@Autowired
	private OfferTypeService service;

	@PostMapping("/paged")
	public ResponseEntity<?> getPaginatedOfferTypes(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken,

			@RequestParam int draw, @RequestParam int start, @RequestParam int length,
			@RequestParam(required = false) String searchText, @RequestParam(defaultValue = "id") String sortColumn,
			@RequestParam(defaultValue = "asc") String sortOrder) {

		Page<OfferType> offerPage = service.getPagedOfferTypes(start, length, searchText, sortColumn, sortOrder);

		Map<String, Object> result = new HashMap<>();
		result.put("draw", draw);
		result.put("recordsTotal", offerPage.getTotalElements());
		result.put("recordsFiltered", offerPage.getTotalElements());
		result.put("data", offerPage.getContent());

		return HttpResponseUtility.getHttpSuccess(result);
	}

	@PostMapping(value = "/insert")
	public ResponseEntity<?> create(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken, @RequestBody OfferTypeRequest offerType) {

		return HttpResponseUtility.getHttpSuccess(service.createOfferType(offerType));
	}

	@GetMapping
	public List<OfferType> getAll() {
		return service.getAllOfferTypes();
	}

	@GetMapping("/type/{id}")
	public ResponseEntity<?> getById(@PathVariable Integer id, @RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken) {
		OfferType offerType = service.getOfferTypeById(id)
				.orElseThrow(() -> new RuntimeException("PromoType is not found for id :" + id));

		return HttpResponseUtility.getHttpSuccess(offerType);

	}

	@PostMapping("/update/{id}")
	public ResponseEntity<?> update(@PathVariable Integer id, @RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken, @RequestBody OfferTypeRequest offerType) {

		return HttpResponseUtility.getHttpSuccess(service.updateOfferType(id, offerType));
	}

	@GetMapping("/delete/{id}")
	public ResponseEntity<?> delete(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken, @PathVariable Integer id) {

		return HttpResponseUtility.getHttpSuccess(service.deleteOfferType(id));
	}

}
