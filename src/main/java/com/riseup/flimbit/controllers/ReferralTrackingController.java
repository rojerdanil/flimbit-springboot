package com.riseup.flimbit.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.entity.ReferralTracking;
import com.riseup.flimbit.service.ReferralTrackingService;

import java.util.List;

@RestController
@RequestMapping("/api/referrals")
public class ReferralTrackingController {

	@Autowired
    ReferralTrackingService referralTrackingService;

    @PostMapping
    public ReferralTracking create(@RequestBody ReferralTracking referralTracking) {
        return referralTrackingService.save(referralTracking);
    }

    @GetMapping("/referrer/{userId}")
    public List<ReferralTracking> getByReferrer(@PathVariable Long userId) {
        return referralTrackingService.getReferralsByReferrer(userId);
    }

    @GetMapping("/referred/{userId}")
    public List<ReferralTracking> getByReferred(@PathVariable Long userId) {
        return referralTrackingService.getReferralsByReferred(userId);
    }
}
