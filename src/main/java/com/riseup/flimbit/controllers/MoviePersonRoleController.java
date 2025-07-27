package com.riseup.flimbit.controllers;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.MoviePersonType;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.MoviePersonRoleService;
import com.riseup.flimbit.utility.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movie-person-role")
public class MoviePersonRoleController {

	@Autowired
	private MoviePersonRoleService service;

	@PostMapping
	public MoviePersonType create(@RequestBody MoviePersonType role) {
		return service.create(role);
	}

	@PutMapping("/{id}")
	public MoviePersonType update(@PathVariable Integer id, @RequestBody MoviePersonType role) {
		return service.update(id, role);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Integer id) {
		service.delete(id);
	}

	@GetMapping("/allRole")
	public ResponseEntity<?> findAll(@RequestHeader(value = "deviceId") String deviceId,
			@RequestHeader(value = "phoneNumber") String phoneNumber,
			@RequestHeader(value = "accessToken") String accessToken) {

		return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder().status(Messages.STATUS_SUCCESS)
				.message(Messages.STATUS_SUCCESS).result(service.findAll()).build());
	}
}
