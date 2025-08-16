package com.riseup.flimbit.response;

import java.util.List;

import com.riseup.flimbit.entity.InvestOfferMoney;
import com.riseup.flimbit.entity.dto.MovieDTO;
import com.riseup.flimbit.entity.dto.UserInvestmentSharTypeDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShareOfferListResponse {
	
	UserInvestmentSharTypeDTO  shareDetail;
	List<InvestOfferMoney> investMoneyList;
	

}
