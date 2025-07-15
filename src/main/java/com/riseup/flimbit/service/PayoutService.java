package com.riseup.flimbit.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.riseup.flimbit.entity.Payout;
import com.riseup.flimbit.entity.dto.PayoutDTO;
import com.riseup.flimbit.entity.dto.UserInvestmentSectionDTO;
import com.riseup.flimbit.request.PayAllShareReturnRequest;
import com.riseup.flimbit.response.SuccessResponse;

public interface PayoutService {
	Page<PayoutDTO>  getMoviePayForUserPayoutSection(int language,int movie,String status,String searchText,int start, int length,  String sortColumn, String sortOrder);
	List<PayoutDTO>  getPayoutForUserIdAndMovieId(int userId,int movId);
	SuccessResponse    saveAllReturnsToUser(PayAllShareReturnRequest request);
	SuccessResponse    saveSpecificShareReturnsToUser(PayAllShareReturnRequest request);


}
