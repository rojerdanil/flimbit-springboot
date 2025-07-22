package com.riseup.flimbit.entity.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Getter
@Setter
public class ChartDTO {
	
	 private List<String> labels;
	    private List<Integer> data;

	    // Constructor
	   


}
