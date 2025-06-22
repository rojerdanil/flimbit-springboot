package com.riseup.flimbit.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.EarningBreakInFace;
import com.riseup.flimbit.entity.MovieInvestSummary;
import com.riseup.flimbit.entity.MovieInvestment;
import com.riseup.flimbit.entity.User;
import com.riseup.flimbit.repository.MovieInvestRepository;
import com.riseup.flimbit.repository.UserRepository;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.EarningBreakResponse;
import com.riseup.flimbit.response.PortFolioResponse;

@Service
public class PortfolioServiceImp implements PortfolioService{
	
	Logger logger
    = LoggerFactory.getLogger(PortfolioService.class);
	
	@Autowired
	UserRepository userRepo;

	@Autowired
	MovieInvestRepository movieInvestRepo;
	
	@Override
	public CommonResponse getPortFolioByUserPhone(String phoneNumber) {
		// TODO Auto-generated method stub
		Optional<User> optUser = userRepo.findByphoneNumber( phoneNumber);
		
		if(optUser.isPresent())
		{
			User user = optUser.get();

    /*			List<MovieInvestment> investList = movieInvestRepo.findByUserId(user.getId());
			if(investList != null)
			{
			projectsInvest = investList.size();
			for(MovieInvestment invest:investList)
			{
				totalInvested = totalInvested + invest.getAmountInvested();
				totalReturns  = totalReturns + invest.getReturnAmount();
				
			}
			}*/
			MovieInvestSummary miSummery = movieInvestRepo.getPortFolioSummary(user.getId());
			
			PortFolioResponse pfRes = PortFolioResponse.builder()
					                 .totalInvested(miSummery.getTotalInvested())
					                 .totalReturns(miSummery.getTotalReturns())
					                 .averageRoi(miSummery.getAverageRoi())
					                 .projectsInvest(miSummery.getProjectsInvested())
					                 .ongoingProjects(miSummery.getOngoingProjects())
					                 .successfulReleases(miSummery.getSuccessfulReleases())
					                 .holdReleases(miSummery.getHoldReleases())
					                 .releasedStageFunds(miSummery.getReleasedFunds())
					                 .ongoingStageFunds(miSummery.getOngoingFunds())
					                 .onHoldStageFunds(miSummery.getHoldingFunds())
					                 .build();
			
			List<EarningBreakInFace>  earnList = movieInvestRepo.getEarningBreak(user.getId());
			if(Optional.ofNullable(earnList).isPresent())
			{
				List<EarningBreakResponse> earnResList  = new ArrayList<EarningBreakResponse>();
				for(EarningBreakInFace earIFace : earnList)
				{
					EarningBreakResponse earnRes = EarningBreakResponse.builder()
							.averageRoi(earIFace.getAverageRoi())
							.invested(earIFace.getInvested())
							.returned(earIFace.getReturned())
							.movieName(earIFace.getMovieName()).build();
					earnResList.add(earnRes);
					
				}
				pfRes.setEarningList(earnResList);

			}
			
			return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message("PortFolio is success" ).object(pfRes).build();
 	
		}
		else
			return CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.REG_PHONE_NUMBER_NOT_FOUND).build();


	}

}
