package com.riseup.flimbit.service;

import com.riseup.flimbit.entity.dto.ChartDTO;
import com.riseup.flimbit.entity.dto.DashboardMetricsDTO;
import com.riseup.flimbit.response.DashBoardChartResponse;

public interface DashboardService {
	
    DashboardMetricsDTO getDashboardMetrics(int languageId);
    
    DashBoardChartResponse  getInvestmentChart();


}
