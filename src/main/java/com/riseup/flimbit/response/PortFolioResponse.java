package com.riseup.flimbit.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter

public class PortFolioResponse {
	BigDecimal totalInvested ;
	BigDecimal totalReturns  ;
	int  projectsInvest ;
	BigDecimal averageRoi ;
	Integer successfulReleases;
	  Integer ongoingProjects;
	  Integer holdReleases;
	  Integer releasedStageFunds;
	  Integer ongoingStageFunds;
	  Integer onHoldStageFunds;
	  
	  List<EarningBreakResponse> earningList ;


}
