package com.riseup.flimbit.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.PortfolioService;
import com.riseup.flimbit.utility.JwtService;

@RestController
@RequestMapping("/portFolio")
public class PortFolioController {
	Logger logger
    = LoggerFactory.getLogger(PortFolioController.class);
	
	@Autowired
	PortfolioService portfolioService;
	
	@GetMapping("/portfolioSummery")
	public ResponseEntity<?> getMsg(@RequestHeader(value="deviceId") String deviceId,
	@RequestHeader(value="phoneNumber") String phoneNumber,
	@RequestHeader(value="accessToken") String accessToken)
	{
        return ResponseEntity.status(HttpStatus.OK).body(portfolioService.getPortFolioByUserPhone(phoneNumber));

	}
}
