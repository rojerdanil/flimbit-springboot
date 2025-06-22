package com.riseup.flimbit.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class EarningBreakResponse {
	String movieName;
	Integer invested;
	Integer returned;
	Double averageRoi;
	

}
