package com.riseup.flimbit.serviceImp;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.dto.ChartDTO;
import com.riseup.flimbit.entity.dto.ChartProjectionDTO;
import com.riseup.flimbit.entity.dto.DashboardMetricsDTO;
import com.riseup.flimbit.repository.UserRepository;
import com.riseup.flimbit.response.DashBoardChartResponse;
import com.riseup.flimbit.service.DashboardService;

@Service
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public DashboardMetricsDTO getDashboardMetrics(int languageId) {
		return userRepository.getDashboardMetrics();
	}

	@Override
	public DashBoardChartResponse getInvestmentChart() {
		// TODO Auto-generated method stub

		List<ChartProjectionDTO> results = userRepository.getInvestedChart();

		List<ChartProjectionDTO> userResult = userRepository.getMonthlyUserRegistrationChart();
		return DashBoardChartResponse.builder()
				.investmentChart(ChartDTO.builder().labels(getLables(results)).data(getData(results)).build())
				.userChart(ChartDTO.builder().labels(getLables(userResult)).data(getData(userResult)).build()).build();

	}

	public List<String> getLables(List<ChartProjectionDTO> listResult) {

		return listResult.stream().map(ChartProjectionDTO::getMonth).collect(Collectors.toList());

	}

	public List<Integer> getData(List<ChartProjectionDTO> listResult) {

		return listResult.stream().map(ChartProjectionDTO::getTotalInvestment).collect(Collectors.toList());

	}
}