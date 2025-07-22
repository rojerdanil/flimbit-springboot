
package com.riseup.flimbit.response;

import com.riseup.flimbit.entity.dto.ChartDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DashBoardChartResponse {

	
	ChartDTO  investmentChart;
	ChartDTO  userChart;
	
	
}
