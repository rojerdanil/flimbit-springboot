package com.riseup.flimbit.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.riseup.flimbit.entity.MovieInvestment;
import com.riseup.flimbit.entity.dto.UserInvestmentSectionDTO;
import com.riseup.flimbit.entity.dto.UserInvestmentSharTypeDTO;
import com.riseup.flimbit.request.MovieInvestRequest;
import com.riseup.flimbit.request.PayAllShareReturnRequest;
import com.riseup.flimbit.request.StatusRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.SuccessResponse;

public interface MovieInvestService {
	
	CommonResponse  getBuyShares(MovieInvestRequest movieInvRequest,String phoneNumber);
	
	Page<UserInvestmentSectionDTO>  getMovieInvestForUserInvestSection(int language,int movie,String status,String searchText,int start, int length,  String sortColumn, String sortOrder);

	
	List<UserInvestmentSharTypeDTO>   readInvestmentWithShareTypeByMovId(int movId,int userId);
	
	MovieInvestment updateInvestmentStatus(StatusRequest statusReq);
	List<UserInvestmentSharTypeDTO>	getInvestmentsForMovIdAndUserIdAndShareTypeId(int id,int userId,int shareId);
	
	SuccessResponse    repayShareInvestMoneyToUser(PayAllShareReturnRequest request);

}
