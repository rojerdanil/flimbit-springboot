package com.riseup.flimbit.entity;

import java.math.BigDecimal;

public interface MovieInvestSummary {
  BigDecimal getTotalInvested();
  BigDecimal getTotalReturns();
  BigDecimal getAverageRoi();
  Integer getProjectsInvested();
  Integer getSuccessfulReleases();
  Integer getOngoingProjects();
  Integer getHoldReleases();
  Integer getReleasedFunds();
  Integer getOngoingFunds();
  Integer getHoldingFunds();

  
}
