package com.riseup.flimbit.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.entity.UserPayoutInitiation;
import com.riseup.flimbit.service.UserPayoutInitiationService;

@RestController
@RequestMapping("/api/user-payouts")
public class UserPayoutInitiationController {
	
    @Autowired
    private UserPayoutInitiationService service;

    @GetMapping("/datatable")
    public List<UserPayoutInitiation> getDataTable(
            @RequestHeader(value = "movieId", required = false) Integer movieId,
            @RequestHeader(value = "shareTypeId", required = false) Integer shareTypeId
    ) {
        return service.findAllFiltered(movieId, shareTypeId);
    }

}
