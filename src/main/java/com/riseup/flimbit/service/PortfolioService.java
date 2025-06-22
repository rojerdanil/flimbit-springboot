package com.riseup.flimbit.service;

import com.riseup.flimbit.response.CommonResponse;

public interface PortfolioService {
	
	public CommonResponse getPortFolioByUserPhone(String phoneNumber);

}
