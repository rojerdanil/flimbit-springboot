package com.riseup.flimbit.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.ReferralTracking;
import com.riseup.flimbit.repository.ReferralTrackingRepository;
import com.riseup.flimbit.service.ReferralTrackingService;

import java.util.List;

@Service
public class ReferralTrackingServiceImpl implements ReferralTrackingService {

	@Autowired
     ReferralTrackingRepository referralTrackingRepository;

   

    @Override
    public List<ReferralTracking> getReferralsByReferrer(Long referrerUserId) {
        return referralTrackingRepository.findByReferrerUserId(referrerUserId);
    }

    @Override
    public List<ReferralTracking> getReferralsByReferred(Long referredUserId) {
        return referralTrackingRepository.findByReferredUserId(referredUserId);
    }

	@Override
	public ReferralTracking save(ReferralTracking referralTracking) {
		// TODO Auto-generated method stub
		return null;
	}
}
