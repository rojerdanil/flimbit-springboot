package com.riseup.flimbit.response.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class RewardTypeAndTargetDtoRes {
	
	List<KeyValueDtoResponse>  types;
	List<KeyValueDtoResponse>  target;


}
