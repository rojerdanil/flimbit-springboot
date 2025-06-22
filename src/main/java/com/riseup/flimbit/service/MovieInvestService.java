package com.riseup.flimbit.service;

import com.riseup.flimbit.request.MovieInvestRequest;
import com.riseup.flimbit.response.CommonResponse;

public interface MovieInvestService {
	
	CommonResponse  getBuyShares(MovieInvestRequest movieInvRequest,String phoneNumber);

}
