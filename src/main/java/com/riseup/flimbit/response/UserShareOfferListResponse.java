package com.riseup.flimbit.response;

import java.util.List;

import com.riseup.flimbit.entity.dto.MovieDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserShareOfferListResponse {
	MovieDTO  movie;
	List<ShareOfferListResponse> shareOffer;
	

}
