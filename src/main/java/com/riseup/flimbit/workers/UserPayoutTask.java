package com.riseup.flimbit.workers;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.MovieProfitPayoutStatus;
import com.riseup.flimbit.constant.PayoutMethod;
import com.riseup.flimbit.constant.PayoutStatus;
import com.riseup.flimbit.entity.Payout;
import com.riseup.flimbit.entity.UserPayoutInitiation;
import com.riseup.flimbit.entity.UserWalletBalance;
import com.riseup.flimbit.repository.PayoutRepository;
import com.riseup.flimbit.repository.UserPayoutInitiationRepository;
import com.riseup.flimbit.repository.UserWalletBalanceRepository;
import com.riseup.flimbit.service.UserWalletBalanceService;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPayoutTask {
	Logger logger = LoggerFactory.getLogger(UserPayoutTask.class);
	@Autowired
	PayoutRepository payoutRepo;
	
	@Autowired
	UserWalletBalanceService userWalletService;
	
	@Autowired
	UserPayoutInitiationRepository userPayoutInitRepo;
	
	@Autowired
    UserWalletBalanceRepository walletRepo;

    @Autowired
    PayoutFailureService  payoutFailureService;

	@Transactional(rollbackFor = Exception.class)
	public void execute(UserPayoutInitiation userPayoutInitiation) throws Exception
	{
		logger.info(" UserPayout Task is started");
		try
		{
			if (PayoutStatus.COMPLETED.name().equalsIgnoreCase(userPayoutInitiation.getStatus())) {
		        logger.warn("Skipping payout for user {} — already completed", userPayoutInitiation.getUserId());
		        return;
		    }

		    Payout payout = Payout.builder()
		            .amount(userPayoutInitiation.getPayoutAmount())
		            .createdAt(new Timestamp(System.currentTimeMillis()))
		            .investmentId(userPayoutInitiation.getInvestmentId())
		            .method(PayoutMethod.WALLET.getMethodName())
		            .movieId(userPayoutInitiation.getMovieId())
		            .note("SYSTEM paid to wallet")
		            .paidAt(new Timestamp(System.currentTimeMillis()))
		            .shareTypeId(userPayoutInitiation.getShareTypeId())
		            .status(PayoutStatus.COMPLETED.name().toLowerCase())
		            .updatedAt(new Timestamp(System.currentTimeMillis()))
		            .userId(userPayoutInitiation.getUserId())
		            .build();

		    payoutRepo.save(payout);

		    // Wallet update
		    addShareCash(payout.getUserId(), payout.getAmount());

		    // Mark initiation as completed
		    userPayoutInitiation.setStatus(PayoutStatus.COMPLETED.name().toLowerCase());
		    userPayoutInitiation.setProcessedOn(new Timestamp(System.currentTimeMillis()));
		    userPayoutInitiation.setPaymentStatus(PayoutStatus.COMPLETED.name().toLowerCase());
		    userPayoutInitRepo.save(userPayoutInitiation);

		    logger.info("Paid to user {} : Amount {}", payout.getUserId(), payout.getAmount());
		    
			
		}
		catch(Exception e)
		{
			logger.error("Error during payout failed job", e);
			
			
			 
			 
			  String errorMsg = e.getMessage();
	           if (errorMsg != null && errorMsg.length() > 200) {
	               errorMsg = errorMsg.substring(0, 200) + "...[truncated]";
	           }
	           userPayoutInitiation.setStatus(MovieProfitPayoutStatus.INITIATED.name().toLowerCase());
	           payoutFailureService. markAsFailed(userPayoutInitiation, errorMsg);
			    throw e;
		}
		
	}
	

	 public void addShareCash(int userId, BigDecimal amount) {
	        UserWalletBalance wallet = getOrCreateWallet(userId);
	        wallet.setShareCashBalance(wallet.getShareCashBalance().add(amount));
	        wallet.setLastUpdated(new Timestamp(System.currentTimeMillis()));
	        walletRepo.save(wallet);
	    }
	 public UserWalletBalance getOrCreateWallet(int userId) {
	        return walletRepo.findByUserId(userId).orElseGet(() -> {
	            UserWalletBalance wallet = UserWalletBalance.builder()
	                    .userId(userId)
	                    .shareCashBalance(BigDecimal.ZERO)
	                    .build();
	            return walletRepo.save(wallet);
	        });
	    }

}
