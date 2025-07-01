package com.riseup.flimbit.service;


import java.util.List;

import com.riseup.flimbit.entity.ReferralTracking;

public interface ReferralTrackingService {
    ReferralTracking save(ReferralTracking referralTracking);
    List<ReferralTracking> getReferralsByReferrer(Long referrerUserId);
    List<ReferralTracking> getReferralsByReferred(Long referredUserId);
}
