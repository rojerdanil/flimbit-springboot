package com.riseup.flimbit.workers;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.riseup.flimbit.constant.MovieProfitPayoutStatus;
import com.riseup.flimbit.entity.UserPayoutInitiation;
import com.riseup.flimbit.repository.UserPayoutInitiationRepository;

@Service
public class PayoutFailureService {
	
	 @Autowired
		UserPayoutInitiationRepository userPayoutInitRepo;

	    @Transactional(propagation = Propagation.REQUIRES_NEW)
	    public void markAsFailed(UserPayoutInitiation initiation, String errorMsg) {
	        initiation.setProcessedOn(new Timestamp(System.currentTimeMillis()));
	        initiation.setPaymentStatus(MovieProfitPayoutStatus.FAILED.name().toLowerCase());
	        initiation.setRemarks(errorMsg);
	        userPayoutInitRepo.save(initiation);
	    }

}
