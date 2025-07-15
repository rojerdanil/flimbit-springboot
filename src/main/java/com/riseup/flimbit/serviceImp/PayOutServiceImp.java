package com.riseup.flimbit.serviceImp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.ActionType;
import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.constant.PayoutMethod;
import com.riseup.flimbit.constant.PayoutStatus;
import com.riseup.flimbit.entity.MovieInvestment;
import com.riseup.flimbit.entity.Payout;
import com.riseup.flimbit.entity.User;
import com.riseup.flimbit.entity.dto.PayoutDTO;
import com.riseup.flimbit.repository.MovieInvestRepository;
import com.riseup.flimbit.repository.PayoutRepository;
import com.riseup.flimbit.repository.UserRepository;
import com.riseup.flimbit.repository.UserStatusRespository;
import com.riseup.flimbit.request.PayAllShareReturnRequest;
import com.riseup.flimbit.response.SuccessResponse;
import com.riseup.flimbit.service.PayoutService;

import ch.qos.logback.core.joran.action.Action;
import jakarta.transaction.Transactional;
@Service
public class PayOutServiceImp implements PayoutService {
	@Autowired
	PayoutRepository  payoutRepository;
	
	@Autowired
	UserRepository userRespository;

	 @Autowired
	 MovieInvestRepository  investRepo;
	
	@Autowired
	AuditLogServiceImp auditService;
	 
	 
	@Override
	public Page<PayoutDTO> getMoviePayForUserPayoutSection(int language, int movie, String status, String searchText,
			int start, int length, String sortColumn, String sortOrder) {
		// TODO Auto-generated method stub
		
	    int page = start / length;
	    
	    System.out.println(language + movie + status);

        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
        Pageable pageable = PageRequest.of(page, length, sort);
        return payoutRepository.getSearchMoviePayForUserPayoutSection(language, movie, status, searchText, pageable);
	}

	@Override
	public List<PayoutDTO> getPayoutForUserIdAndMovieId(int userId, int movId) {
		// TODO Auto-generated method stub
		return payoutRepository.getPayoutForUserIdAndMovieId(userId, movId);
	}
	
	
    @Transactional
	@Override
	public SuccessResponse    saveAllReturnsToUser(PayAllShareReturnRequest request)
	{
		// TODO Auto-generated method stub

	 // User user =	userRespository.findById(request.getUserId())
		//   .orElseThrow(() -> new RuntimeException("User is not found for userId "+request.getUserId() ));
		
	List<MovieInvestment> investList =	investRepo.findByUserIdAndMovieId(request.getUserId(),request.getMovieId())
		.orElseThrow(() -> new RuntimeException("Investment is not found for userId  "+request.getUserId() + " movie id " + request.getMovieId()  ));
		
	savePayoutForInvestment(investList);
	  
	     //System.out.println(totalReturns + ": " + request.getAmount());
		return SuccessResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_SUCCESS).build();
	}
    
    
    public  void savePayoutForInvestment(List<MovieInvestment>  investList) 
    {
    	BigDecimal totalReturns = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
    	
  	  for(MovieInvestment invest : investList )
  	  {
  		       
          		
  		    List<Payout> listPayout = payoutRepository.findByUserIdAndMovieIdAndShareTypeIdAndInvestmentId
  		                                 (invest.getUserId(), invest.getMovieId(), invest.getShareTypeId(),invest.getId()).get();
  	  
  		  
  		    
  		    if(listPayout == null || listPayout.size() == 0)
  		    {
  		    	
  		    	// No payment for found  
  		    	totalReturns =	totalReturns.add(invest.getReturnAmount());
  		        Payout payout =	createNewPayoutForInvestment(invest,invest.getReturnAmount(),PayoutStatus.PAID.name(),PayoutMethod.WALLET.getMethodName(),"Manually credited by admin") ;        	 
  		        payoutRepository.save(payout);
  		        invest.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
  		        invest.setReturnAmount(payout.getAmount());
  		        investRepo.save(invest);
  		     //  auditService.logAction(0,ActionType.CREATE.name() , EntityName.PAYOUT.name(), , null, totalReturns);
  		    
  		    }
  		    else
  		    {
  		       BigDecimal returnAmount	= invest.getReturnAmount();
  		       
  		       BigDecimal payoutTotalAmount = listPayout.stream()
  		    		    .map(Payout::getAmount)
  		    		    .filter(amount -> amount != null) // optional: to avoid NullPointerException
  		    		    .reduce(BigDecimal.ZERO, BigDecimal::add);	
  		       
  		       if(returnAmount.compareTo(payoutTotalAmount) == 0)
  		       {
  		    	   // amount already paid equal
  		    	   //System.out.println("amount paid already " +  invest.getId() );
  		    	   //System.out.println("amount " +  returnAmount + " : " +  payoutTotalAmount);
  		    	   throw new RuntimeException("Already paid informed to secuiry risk team");

  		    	   
              
  		       }
  		       else if(returnAmount.compareTo(payoutTotalAmount) > 0)
  		       {
  		    	     // returnabmount greater payoutTotalAmount
  		    	   //System.out.println("amount  should be paid  " +  invest.getId() );

  		    	  // System.out.println("amount : " + returnAmount.subtract(payoutTotalAmount));
  		    	   
  		    	   totalReturns =  totalReturns.add(returnAmount.subtract(payoutTotalAmount)) ; 
  		    	 
  		    	   
  		    	    Payout payout =	createNewPayoutForInvestment(invest,returnAmount.subtract(payoutTotalAmount),PayoutStatus.PAID.name(),PayoutMethod.WALLET.getMethodName(),"Manually credited by admin") ;        	 
  			        payoutRepository.save(payout);
  			        invest.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
  			        invest.setReturnAmount(payout.getAmount());
  			        investRepo.save(invest);
  		    	   

  		       }
  		       else if(returnAmount.compareTo(payoutTotalAmount) < 0)
  		       {
  		    	     // returnabmount lessthan payoutTotalAmount
  		    	   //System.out.println("paid amount greater please do nessarey  " +  invest.getId() );
                    
  		    	   //System.out.println("amount : " + payoutTotalAmount.subtract(returnAmount));
  		    	   throw new RuntimeException("We found that payout amt is higer than returns "+ invest.getId());
  		       }
    
  		       
  		    	
  		    }
  	  
  	  
  	  }
    	
    	
    	
    }
    
    
    public Payout  createNewPayoutForInvestment(MovieInvestment investment,BigDecimal amount,
    		String status,String methord,String note)
    {
    	Payout payout = new Payout();
    	payout.setAmount(amount);
    	payout.setInvestmentId(investment.getId());
    	payout.setMethod(methord);
    	payout.setMovieId(investment.getMovieId());
    	payout.setNote(methord);
    	payout.setNote(note);
    	payout.setShareTypeId(investment.getShareTypeId());
    	payout.setStatus(status);
    	payout.setUserId(investment.getUserId());
    	return payout;
    	
    }

	@Override
	public SuccessResponse saveSpecificShareReturnsToUser(PayAllShareReturnRequest request) {
		// TODO Auto-generated method stub
		MovieInvestment invest =	investRepo.findByUserIdAndMovieIdAndShareTypeId(request.getUserId(),request.getMovieId(),request.getShareTypeId())
				.orElseThrow(() -> new RuntimeException("Investment is not found for userId  "+request.getUserId() + " movie id " + request.getMovieId()  ));
				
		List<MovieInvestment> investList = new ArrayList<MovieInvestment>();
		investList.add(invest);
		savePayoutForInvestment(investList);
			  
			     //System.out.println(totalReturns + ": " + request.getAmount());
				return SuccessResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_SUCCESS).build();
	}

}
