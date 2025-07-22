package com.riseup.flimbit.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.service.DashboardService;
import com.riseup.flimbit.utility.HttpResponseUtility;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping(value = "/readDashBoard")
    public  ResponseEntity<?> getDashboardMetrics() {
    
    	return HttpResponseUtility.getHttpSuccess(dashboardService.getDashboardMetrics(0));
    }
    
    
    @GetMapping(value = "/dashBoardChart")
    public  ResponseEntity<?> getSashBoardChart() {
    
    	return HttpResponseUtility.getHttpSuccess(dashboardService.getInvestmentChart());
    }
    
    
    
    
}
