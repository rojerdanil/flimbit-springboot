package com.riseup.flimbit.serviceImp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.Bidi;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.ActionType;
import com.riseup.flimbit.constant.ConfigCacheService;
import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.constant.PaymentStatus;
import com.riseup.flimbit.constant.PayoutStatus;
import com.riseup.flimbit.constant.StatusEnum;
import com.riseup.flimbit.entity.MovieInvestment;
import com.riseup.flimbit.entity.MoviePaymentTransaction;
import com.riseup.flimbit.entity.MovieShareType;
import com.riseup.flimbit.entity.Payout;
import com.riseup.flimbit.entity.SystemSettings;
import com.riseup.flimbit.entity.InvestOfferMoney;
import com.riseup.flimbit.entity.InvestmentStatus;
import com.riseup.flimbit.entity.Movie;
import com.riseup.flimbit.entity.User;
import com.riseup.flimbit.entity.dto.MovieDTO;
import com.riseup.flimbit.entity.dto.UserInvestmentSectionDTO;
import com.riseup.flimbit.entity.dto.UserInvestmentSharTypeDTO;
import com.riseup.flimbit.entity.dto.UserMoviePurchaseProjection;
import com.riseup.flimbit.gateway.payment.PaymentFactory;
import com.riseup.flimbit.gateway.payment.PaymentOrderResponse;
import com.riseup.flimbit.gateway.payment.PaymentService;
import com.riseup.flimbit.repository.InvestOfferMoneyRepository;
import com.riseup.flimbit.repository.MovieInvestRepository;
import com.riseup.flimbit.repository.MoviePaymentTransactionRepository;
import com.riseup.flimbit.repository.MovieShareTypeRepository;
import com.riseup.flimbit.repository.MoviesRepository;
import com.riseup.flimbit.repository.SystemSettingsRepository;
import com.riseup.flimbit.repository.UserRepository;
import com.riseup.flimbit.request.MovieIinvestSuccess;
import com.riseup.flimbit.request.MovieInvestRequest;
import com.riseup.flimbit.request.MovieOfferCalculatorRequest;
import com.riseup.flimbit.request.MovieRequest;
import com.riseup.flimbit.request.PayAllShareReturnRequest;
import com.riseup.flimbit.request.PaymentUpdateRequest;
import com.riseup.flimbit.request.SharePaymentRequest;
import com.riseup.flimbit.request.StatusRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.PaymentFeeResponse;
import com.riseup.flimbit.response.ShareOfferListResponse;
import com.riseup.flimbit.response.SharePaymentResponse;
import com.riseup.flimbit.response.SuccessResponse;
import com.riseup.flimbit.response.UserShareOfferListResponse;
import com.riseup.flimbit.response.SharePaymentResponse;
import com.riseup.flimbit.response.dto.AuditPair;
import com.riseup.flimbit.response.dto.InvestOfferMoneyDTO;
import com.riseup.flimbit.response.dto.OfferMoneyResponse;
import com.riseup.flimbit.security.UserContext;
import com.riseup.flimbit.security.UserContextHolder;
import com.riseup.flimbit.service.MovieInvestService;
import com.riseup.flimbit.service.SystemSettingsService;
import com.riseup.flimbit.service.UserWalletBalanceService;
import com.riseup.flimbit.utility.OfferMoneyCalculator;
import com.riseup.flimbit.utility.TaxCalculator;

import jakarta.transaction.Transactional;

@Service
public class MovieInvestServiceImp implements MovieInvestService {
	Logger logger = LoggerFactory.getLogger(MovieInvestServiceImp.class);
	@Autowired
	MoviesRepository movieRepository;

	@Autowired
	MovieInvestRepository movieInvestRepo;

	@Autowired
	UserRepository userRepo;
	@Autowired
	AuditLogServiceImp aduit;

	@Autowired
	OfferMoneyCalculator offerMoneyCalculator;
	
	@Autowired
	InvestOfferMoneyRepository investMoneyRepo;
	
	
	@Autowired
	UserWalletBalanceService userWalletService;
	
	@Autowired
   SystemSettingsRepository  systemRepository;
	
	 @Autowired
	 ConfigCacheService  configCacheService;

	 @Autowired
	 MovieShareTypeRepository movShareTyeRepo;
	 
	 @Autowired
	 PaymentFactory  paymentFactory;
	 
	 @Autowired
	    private SystemSettingsService systemSettingsService;
	 @Autowired
	   private MoviePaymentTransactionRepository paymentTransactionRepository;
	 
	 @Value("${default_active_payment_gateway}")
	    String defaultPaymentProvider;
	 
	 @Autowired
	 TaxCalculator taxCalculator;
	    
	    @Transactional
		@Override
		public SharePaymentResponse validatePaymentForShares(SharePaymentRequest sharePaymentRequest) {
			// TODO Auto-generated method stub
	    
			logger.info(
					"User trying to invest in movie  " + sharePaymentRequest.getMovieId() + " :offer Id: " +sharePaymentRequest.getShareTypeId()  
					+"(validatePaymentForShares) : " + UserContextHolder.getContext().getPhone());
			
			
			UserContext  loginUser = UserContextHolder.getContext();
			
			if(loginUser == null)
				throw new RuntimeException("unable to find login uses please relogin ");
			
			logger.info(" User id " + loginUser.getUserId()  );
			
			if(sharePaymentRequest == null)
				throw new RuntimeException("Request object can not be empty ");

			
			
			OfferMoneyResponse  offerMoneyRequestOld = sharePaymentRequest.getOfferMoneyResponse();			

			if(sharePaymentRequest.getNumberOfShares() != offerMoneyRequestOld.getTotalShare())
			{
				  throw new RuntimeException("Share availability has changed since your last calculation. Please recheck the updated total shares before confirming your purchase.");
				  
			}
			
			
		
			
			
			if(offerMoneyRequestOld.isUserReachedMaxInvest())
				  throw new RuntimeException("You’ve reached your yearly investment threshold. To continue investing, please complete PAN verification with video KYC.");

				
					

			
			OfferMoneyResponse offerMoneyNewCalculate = null;
			
			
			if(offerMoneyRequestOld.isOfferAvailable())
			{
				logger.info(" offer available ");
				 Optional<SystemSettings> sysSettingOPt   =	systemRepository.findByKeyIgnoreCaseAndGroupNameIgnoreCase("offer_active", EntityName.OFFER.name());

				 boolean isOfferGlobleEnable =  sysSettingOPt.isPresent() && sysSettingOPt.get().getValue().equalsIgnoreCase(StatusEnum.ACTIVE.name())
						                        ? true : false;
				 logger.info("Is offer_active  " + isOfferGlobleEnable);
				 
	
				if(isOfferGlobleEnable  !=  offerMoneyRequestOld.isOfferGlobleEnable() )
					  throw new RuntimeException("The selected offer is no longer valid due to updated conditions. Please review the latest offer details and proceed again.");

				
				
				MovieOfferCalculatorRequest offerCalRequest = MovieOfferCalculatorRequest.builder()
						.movieId(sharePaymentRequest.getMovieId())
						.numberOfShares(sharePaymentRequest.getNumberOfShares())
						.promoCode(sharePaymentRequest.getPromoCode())
						.shareTypeId(sharePaymentRequest.getShareTypeId()).build();

				 offerMoneyNewCalculate = offerMoneyCalculator
						.getoOfferAmountForMovieAndShareTypeId(offerCalRequest,true,loginUser.getUserId());

				 
					if(offerMoneyNewCalculate.isUserReachedMaxInvest())
						  throw new RuntimeException("You’ve reached your yearly investment threshold. To continue investing, please complete PAN verification with video KYC.");

				 
								verifyOfferMoneyCorrect(offerMoneyNewCalculate,offerMoneyRequestOld);
				
				
				 // addOfferAmountToUserPayement(offerMoneyNewCalculate,loginUser.getUserId(),sharePaymentRequest.getShareTypeId());
				
                  logger.info("Movie share eligble for payments ");				
                  
         
                  }
			
			
			BigDecimal  investedAmount = offerMoneyRequestOld.getTotalOrignalAmount();
			
			int totalShare = offerMoneyRequestOld.getTotalShare();
			
			if(offerMoneyRequestOld.isOfferAvailable())
			{
				investedAmount= offerMoneyRequestOld.getNewTotalAfterOffer();
				totalShare = totalShare + offerMoneyRequestOld.getTotalFreeShare();
			}
			
			PaymentFeeResponse paymentFessTax	=  taxCalculator.getPaymentTax(investedAmount, sharePaymentRequest.getPaymentMethod());
			
			logger.info("paymentFessTax "+ paymentFessTax.getTotalPayable() + ":new: "  +paymentFessTax.getTotalPayable() );
			if(sharePaymentRequest.getTotalPayable().compareTo(paymentFessTax.getTotalPayable()) !=  0)
			{
				logger.info("Calculation changed, please retry Requsest Payable " + sharePaymentRequest.getTotalPayable()
				          +  " : Newly Calcualted " + paymentFessTax.getTotalPayable());
				
				throw new RuntimeException("Payment amount has changed due to updated fees or settings. Please try again.");
				
			}
			
			
			
			 String activeProvider = Objects.requireNonNullElse(
			            systemSettingsService.getValue("default_active_payment_gateway", EntityName.PAYMENT.name()),
			            defaultPaymentProvider
			        );
			
			 PaymentService  paymentService = paymentFactory.getPaymentService();
             String orderIdRef = "FILMBIT-U"+ loginUser.getUserId() + LocalDate.now() + "-" + UUID.randomUUID().toString().substring(0,8);
          
             try {
				String orderId = paymentService.createOrder(orderIdRef, sharePaymentRequest.getTotalPayable(),"INR",null);
            	// String paymentId  = "test";
				MoviePaymentTransaction  paymentTrasaction = MoviePaymentTransaction.builder()
						           .investmentId(null)
						           .movieId(sharePaymentRequest.getMovieId())
						           .orderId(orderId)
						           .userId(loginUser.getUserId())
						           .paymentGateway(activeProvider)
						           .amount(investedAmount)
						           .paymentId(null)
						           .shareTypeId(sharePaymentRequest.getShareTypeId())
						           .status(PaymentStatus.PENDING.name())
						           .createdAt(new Timestamp(System.currentTimeMillis()))
						           .updatedAt(new Timestamp(System.currentTimeMillis()))
						           .totalShare(totalShare)
						           .perShareAmount(offerMoneyRequestOld.getPerShareAmount())
						           .internalOrderRef(orderIdRef)
						           .build();
						           
				int transactionId = paymentTransactionRepository.save(paymentTrasaction).getId();
				
		
				SharePaymentResponse sharPaymentResponse = SharePaymentResponse.builder().orderNo(orderId)
						          .status(PaymentStatus.PENDING.name()).build();
				
				
			   if(offerMoneyNewCalculate != null)
			   { 
				if(offerMoneyNewCalculate.getInvestOFferList() != null && offerMoneyNewCalculate.getInvestOFferList().size() > 0)
				{
					logger.info("Enter into save investOFferMoney ");
					List<InvestOfferMoney>  investOfferList =  mapDtoListToEntityList(offerMoneyNewCalculate.getInvestOFferList()
						,null,loginUser.getUserId(),sharePaymentRequest.getShareTypeId(),PaymentStatus.PENDING.name(),transactionId,orderId);
					investMoneyRepo.saveAll(investOfferList);
					
				}
			   }
				return sharPaymentResponse;
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
               throw new RuntimeException("Error in payment gatway service . please wait" );			
			}
			
             
		//calculate user has reached his limit amount
			
					
		}

		public void  addOfferAmountToUserPayement( OfferMoneyResponse offerMoneyResponse,int userId,int shareTypeId )
		{

							
	        	            
				if(offerMoneyResponse.getInvestOFferList() != null && offerMoneyResponse.getInvestOFferList().size() > 0)
				{
					logger.info("Enter into save investOFferMoney ");
					//List<InvestOfferMoney>  investOfferList =  mapDtoListToEntityList(offerMoneyResponse.getInvestOFferList()
						//	,null,userId,shareTypeId);
					//investMoneyRepo.saveAll(investOfferList);
					
				}

				
					
		}

		
		
		


	
	public MovieInvestment  addOfferAmountToUser( OfferMoneyResponse offerMoneyResponse ,MovieInvestment movieInves,MovieInvestRequest movieInvRequest
			,int perShareAmount,int userId,boolean isExistingInvestment)
	{

		int userInvest = perShareAmount * movieInvRequest.getNumberOfShares();

		int freeShare = 0;
		
		BigDecimal amountInvested = BigDecimal.valueOf(userInvest);
		
		
		
	
		if(offerMoneyResponse != null)
		{
			 Optional<SystemSettings> sysSettingOPt   =	systemRepository.findByKeyIgnoreCaseAndGroupNameIgnoreCase("offer_active", EntityName.OFFER.name());

			 boolean isOfferGlobleEnable =  sysSettingOPt.isPresent() && sysSettingOPt.get().getValue().equalsIgnoreCase(StatusEnum.ACTIVE.name())
					                        ? true : false;
			 logger.info("Is offer disabled offer_active  " + isOfferGlobleEnable);
		
			
			if(offerMoneyResponse.isOfferAvailable() == true && isOfferGlobleEnable)
			{

		    if(offerMoneyResponse.getTotalDiscountAmount().compareTo(BigDecimal.valueOf(0) )> 0)
			{
			logger.info("Offer found so amount should be reduced for discuount amount");
			logger.info("original amount " + amountInvested  + " discount " + offerMoneyResponse.getTotalDiscountAmount());
			amountInvested = amountInvested.subtract(offerMoneyResponse.getTotalDiscountAmount());
			logger.info("after discoun original amount " + amountInvested );

			
			}
			if(offerMoneyResponse.isPlatformCommision())
			{
				
				int newNoPlatformShare = 	movieInves.getTotalShareNoPlatformCommission() +movieInvRequest.getNumberOfShares();
				movieInves.setTotalShareNoPlatformCommission(newNoPlatformShare);
					
			}
			if(offerMoneyResponse.isProfitCommission())
			{
				int newNoProfitCommision = movieInves.getTotalShareNoProfitCommission() + +movieInvRequest.getNumberOfShares();
				movieInves.setTotalShareNoProfitCommission(newNoProfitCommision);
			}
		
			if(offerMoneyResponse.getTotalWalletAmount().compareTo(BigDecimal.valueOf(0)) > 0)
			{
				
			}
            if(offerMoneyResponse.getTotalFreeShare() != 0  &&  offerMoneyResponse.getIsBuyAndGet())
            {
            	
            	freeShare =  offerMoneyResponse.getTotalFreeShare();
            	

            }
			
            if(offerMoneyResponse.getTotalWalletAmount().compareTo(BigDecimal.valueOf(0)) > 0)
            {
            	logger.info("Wallet amount is going to add");
            	aduit.logAction(userId, ActionType.CREATE.name(),
						EntityName.USER_WALLET.name(), movieInves.getId(),
						"New amount is added  " + UserContextHolder.getContext().getPhone(),
						movieInvRequest);
            	
            	userWalletService.addShareCash(userId, offerMoneyResponse.getTotalWalletAmount());
            	
            }
            
            
			if(offerMoneyResponse.getInvestOFferList() != null && offerMoneyResponse.getInvestOFferList().size() > 0)
			{
				logger.info("Enter into save investOFferMoney ");
				
				//List<InvestOfferMoney>  investOfferList =  mapDtoListToEntityList(offerMoneyResponse.getInvestOFferList()
				//		,movieInves.getId(),userId,movieInves.getShareTypeId());
				//investMoneyRepo.saveAll(investOfferList);
				
			}
			
			}

			
		}
		
    	movieInves.setNumberOfShares(movieInves.getNumberOfShares() +  movieInvRequest.getNumberOfShares() + freeShare );

		
		if(isExistingInvestment)
		movieInves.setAmountInvested(movieInves.getAmountInvested().add(amountInvested));
		else
			movieInves.setAmountInvested(amountInvested);	
		
		return movieInves;

		
	}
	
	
	public  List<InvestOfferMoney> mapDtoListToEntityList(List<InvestOfferMoneyDTO> dtoList,Integer investId
			,int userId,int shareTypeId,String status,int transectionId,String orderId) {
        if (dtoList == null || dtoList.isEmpty()) {
            return List.of();
        }

        return dtoList.stream()
                .map(dto -> InvestOfferMoney.builder()
                        .investId(investId)
                        .offerId((int) dto.getOfferId()) // long → int conversion
                        .discountAmount(dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO)
                        .walletAmount(dto.getWalletAmount() != null ? dto.getWalletAmount() : BigDecimal.ZERO)
                        .isNoPlatformCommission(dto.isNoPlatformCommision())
                        .isNoProfitCommission(dto.isNoProfitCommission())
                        .freeShare(dto.getFreeShare())
                        .offerName(dto.getOfferName())
                        .createdAt(new Timestamp(System.currentTimeMillis()))
                        .updatedAt(new Timestamp(System.currentTimeMillis()))
                        .movieId(dto.getMovId())
                        .totalShare(dto.getTotalShare())
                        .userId(userId)
                        .shareTypeId(shareTypeId)
                        .status(status)
                        .transactionId(transectionId)
                        .orderId(orderId)
                        .build())
                .collect(Collectors.toList());
    }
	
	public boolean  verifyOfferMoneyCorrect(OfferMoneyResponse offerMoneyResponse,OfferMoneyResponse movieInvRequest)
	{
		
		
		
		
		Boolean isOfferMisMatched = false;
		
		
		
		
		
		if (offerMoneyResponse != null) {
			if (offerMoneyResponse.getTotalDiscountAmount()
					.compareTo(movieInvRequest.getTotalDiscountAmount()) != 0) {
				logger.info("TotalDiscountAmount  is mismatched req"
						+ movieInvRequest.getTotalDiscountAmount() + " Newly Calculated :"
						+ offerMoneyResponse.getTotalDiscountAmount());
				isOfferMisMatched = true;
			}
			if (offerMoneyResponse.getTotalFreeShare() != movieInvRequest.getTotalFreeShare()) {
				logger.info("TotalFreeShare  is mismatched req "
						+ movieInvRequest.getTotalFreeShare() + " Newly Calculated :"
						+ offerMoneyResponse.getTotalDiscountAmount());

				isOfferMisMatched = true;

			}
			if (offerMoneyResponse.getTotalWalletAmount()
					.compareTo(movieInvRequest.getTotalWalletAmount()) != 0) {
				logger.info("TotalWalletAmount  is mismatched req " 

						+ "  Newly Calculated :" + offerMoneyResponse.getTotalWalletAmount());

				isOfferMisMatched = true;
			}
			if (offerMoneyResponse.isPlatformCommision() != movieInvRequest.isPlatformCommision()) {
				logger.info("PlatformCommision  is mismatched req "
						+ movieInvRequest.isPlatformCommision() + " Newly Calculated : "
						+ offerMoneyResponse.isPlatformCommision());

				isOfferMisMatched = true;

			}

			if (offerMoneyResponse.isProfitCommission() != movieInvRequest.isProfitCommission()) {
				logger.info("sProfitCommission  is mismatched Req "
						+ movieInvRequest.isProfitCommission() + " Newly Calculated :"
						+ offerMoneyResponse.isProfitCommission());

				isOfferMisMatched = true;

			}
			if(offerMoneyResponse.getNewTotalAfterOffer().compareTo( movieInvRequest.getNewTotalAfterOffer()) != 0)
			{
				
				logger.info("NewTotalAfterOffer  is mismatched Req "
						+ movieInvRequest.getNewTotalAfterOffer() + " Newly Calculated :"
						+ offerMoneyResponse.getNewTotalAfterOffer());

				isOfferMisMatched = true;
			}

		}

		logger.info("Movie OFfer verfication ends verfied  " + isOfferMisMatched);
		
	  if(isOfferMisMatched)
	  {
		  aduit.logActionNoTrx(UserContextHolder.getContext().getUserId(), ActionType.MISMATCH.name(),
					EntityName.OFFER_CACCULATE.name(), 0,
					"Offer is mismathced " + UserContextHolder.getContext().getPhone(),
					AuditPair.builder().sourcne(movieInvRequest).destination(offerMoneyResponse).build());
		  throw new RuntimeException("The offer or price has changed since your last calculation. Please review the updated price before confirming your purchase");
	  }
     return isOfferMisMatched;
		
	}

	@Override
	public Page<UserInvestmentSectionDTO> getMovieInvestForUserInvestSection(int language, int movie, String status,
			String searchText, int start, int length, String sortColumn, String sortOrder) {
		// TODO Auto-generated method stub

		int page = start / length;
		Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
		Pageable pageable = PageRequest.of(page, length, sort);
		return movieInvestRepo.getSearchMovieInvForUserInvestSection(language, movie, status, searchText, pageable);
		/*
		 * System.out.println("total records "+ pageRecords.getTotalElements());
		 * if(pageRecords.getTotalElements() > 0) return pageRecords;
		 */
		// return movieInvestRepo.findSearchMovieInvForAllUser(pageable);
	}

	@Override
	public List<UserInvestmentSharTypeDTO> readInvestmentWithShareTypeByMovId(int movId, int userId) {
		// TODO Auto-generated method stub

		List<UserInvestmentSharTypeDTO> list = movieInvestRepo.getInvestmentsWithShareTypeByMovId(movId, userId)
				.orElseThrow(() -> new RuntimeException("No investment details for movie " + movId));

		return list;
	}

	@Override
	public MovieInvestment updateInvestmentStatus(StatusRequest statusReq) {
		// TODO Auto-generated method stub
		MovieInvestment movieInvest = movieInvestRepo.findById(statusReq.getId())
				.orElseThrow(() -> new RuntimeException("Investment id is not found " + statusReq.getId()));

		if (movieInvest.getStatus().equals(statusReq.getStatus()))
			throw new RuntimeException("status is same can not be changed");

		// movieInvest.setStatus(statusReq.getStatus().toLowerCase());
		movieInvest.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
		InvestmentStatus invesStatus = new InvestmentStatus();
		invesStatus.setInvestmentId(statusReq.getId());
		invesStatus.setDescription(statusReq.getDescription());
		invesStatus.setStatus(statusReq.getStatus());

		return movieInvestRepo.save(movieInvest);

	}

	@Override
	public List<UserInvestmentSharTypeDTO> getInvestmentsForMovIdAndUserIdAndShareTypeId(int movId, int userId,
			int shareId) {
		// TODO Auto-generated method stub
		List<UserInvestmentSharTypeDTO> list = movieInvestRepo
				.getInvestmentsForMovIdAndUserIdAndShareTypeId(movId, userId, shareId)
				.orElseThrow(() -> new RuntimeException("No investment details for movie " + movId));

		return list;
	}

	@Transactional
	@Override
	public SuccessResponse repayShareInvestMoneyToUser(PayAllShareReturnRequest request) {
		// TODO Auto-generated method stub

		MovieInvestment investment = movieInvestRepo
				.findByUserIdAndMovieIdAndShareTypeId(request.getUserId(), request.getMovieId(),
						request.getShareTypeId())
				.orElseThrow(() -> new RuntimeException(
						"No investment details for Share type  " + request.getShareTypeId()));

		BigDecimal repayAmount = BigDecimal.valueOf(request.getAmount());

		if (repayAmount.compareTo(investment.getAmountInvested()) == 0) {

			investment.setAmountInvested(BigDecimal.valueOf(0));
			investment.setNumberOfShares(0);
			investment.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
			investment.setReturnAmount(BigDecimal.valueOf(0));
			movieInvestRepo.save(investment);

		}

		return SuccessResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_SUCCESS).build();
	}

	@Override
	public OfferMoneyResponse getCalculateOfferMoney(MovieOfferCalculatorRequest movieInvRequest) {
		// TODO Auto-generated method stub
		logger.info("***Get Calculated called for user starts *** " + UserContextHolder.getContext().getPhone());
         UserContext  loginUser = UserContextHolder.getContext();
		logger.info("***Get Calculated called for user ends *** ");
		return offerMoneyCalculator.getoOfferAmountForMovieAndShareTypeId(movieInvRequest,false,loginUser.getUserId());

	}

	
	@Transactional
	@Override
	public void updatePaymentStatus(PaymentUpdateRequest request) {
		// TODO Auto-generated method stub
		logger.info("*****update payment status stars *****");
		logger.info("** input orderId ****" + request.getOrderId() + "* : payment id :*" + request.getPaymentId()
		            + " :PaymentMethod :" + request.getPaymentMethod()
		         + " :Singnature :" +request.getSignature()    
				);
		
		if (request.getSignature() == null || request.getSignature().isEmpty()) {
		    logger.info("Missing signature. Cannot verify payment.");
		    return;
		}
		
		 PaymentService  paymentService = paymentFactory.getPaymentService();
 /*         boolean  isPaymentVerified = paymentService.verifyPayment(request.getPaymentId(),
        		 request.getSignature(), request.getOrderId());
         
        
          
         logger.info("isPaymentVerified :" +  isPaymentVerified); 
          
         if(!isPaymentVerified)
         {
        	    logger.warn("❌ Missing signature for orderId: {}. Stopping payment verification.", request.getOrderId());
        	 aduit.logActionNoTrx(0, ActionType.CRITICAL.name(),
 					EntityName.PAYMENT_FAILURE.name(),0,
 					"Payment signature is  triying to compramise " + request.getOrderId()  + ":" +request.getPaymentId(),
 					request);

        	 
        	 return;
         }
           */
         PaymentOrderResponse  paymentOrderResponse = null;
         
         try {
        	 paymentOrderResponse
        	 	 = paymentService.getOrder(request.getOrderId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			aduit.logActionNoTrx(0, ActionType.CRITICAL.name(),
					EntityName.PAYMENT_FAILURE.name(),0,
					"Payment order is not found " + request.getOrderId()  + ":" +request.getPaymentId(),
					request);

			return ;
       	 //throw  new RuntimeException("Failed to read Order from payemet "  +  request.getOrderId());

		}
         
        String paymentStatus = PayoutStatus.PENDING.name();
        
        if(paymentOrderResponse != null)
        {
        	logger.info("Payment satus : " + paymentOrderResponse.getStatus());
        	paymentStatus  = paymentOrderResponse.getStatus().toLowerCase();
        }
         
         
         
		MoviePaymentTransaction paymentTransection = paymentTransactionRepository.findByOrderId(request.getOrderId());
		
		
		
		if(paymentTransection == null)
		{
			logger.info("**Payment trasection is not found for " + request.getOrderId());
			//throw new RuntimeException("Transection is  not found");
			return ;
		}
		
	   logger.info("paymentTransection check status " +paymentTransection.getStatus());
	
	   boolean isPendingTrxPaid = paymentTransection.getStatus().equalsIgnoreCase(PayoutStatus.PENDING.name()) &&
			    (paymentStatus.equalsIgnoreCase("PAID") || paymentStatus.equalsIgnoreCase("CAPTURED")) ;
		
	   if (isPendingTrxPaid)
	   {
        	 logger.info("Started updating payment details in investment ");
        	
        	 int movieId = paymentTransection.getMovieId();
        	 int userId = paymentTransection.getUserId();
        	 int shareTypeId = paymentTransection.getShareTypeId();
        	 
        	 
        	 logger.info("stated reading movie information");
				Optional<MovieInvestment> movieInvesOpt = movieInvestRepo.findByUserIdAndMovieIdAndShareTypeId(
						userId,movieId, shareTypeId);
        	 
				MovieInvestment movieInves  = new MovieInvestment();
				
				if(movieInvesOpt.isPresent())
				{
					movieInves =  movieInvesOpt.get();     	 

				}
				else
				{
					movieInves.setInvestedAt(new Timestamp(System.currentTimeMillis()));
					movieInves.setMovieId(movieId);	
					movieInves.setShareTypeId(shareTypeId);
					movieInves.setStatus(StatusEnum.ACTIVE.name());
					movieInves.setUserId(paymentTransection.getUserId());
			    }
        	           
				 logger.info("stated reading movie information");
					
				aduit.logAction(0, ActionType.UPDATE.name(),
						EntityName.MOVIE_INVESTMENT.name(), movieInves.getId(),
						"new investment is added" ,
						movieInves);
				
				//   	movieInves.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
				

				
				MovieIinvestSuccess invesSucces = MovieIinvestSuccess.builder()
				.movieId(movieInves.getMovieId())
				.numberOfNewShares(paymentTransection.getTotalShare() )
				.perShareAmount(paymentTransection.getPerShareAmount())
				.numberOfOldShares(movieInves.getNumberOfShares())
				.oldShareAmount(movieInves.getAmountInvested()).build();
	
        	 
        	  int newShare = movieInves.getNumberOfShares() +  paymentTransection.getTotalShare();
        	  movieInves.setNumberOfShares(newShare);
        	  movieInves.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        	  movieInves.setAmountInvested(movieInves.getAmountInvested().add(paymentTransection.getAmount()));
        	  movieInves = movieInvestRepo.save(movieInves);

              logger.info("Calculating Wallet offer Amount to be send ......");        	  
              BigDecimal offerWalletMoney = investMoneyRepo.getTotalWalletAmountByOrderId(paymentTransection.getOrderId());  
              logger.info("Walled discount is found " + offerWalletMoney);
              if (offerWalletMoney != null && offerWalletMoney.compareTo(BigDecimal.ZERO) > 0) {
            	    // Wallet money is greater than zero
            	  logger.info("Entering into wallet credit " + userId + " : amount :" + offerWalletMoney);
               userWalletService.addShareCash(userId,offerWalletMoney);

            	}
              
              
            logger.info("savving into PaymentTransection and invest_money_offer"); 
            investMoneyRepo.updateStatusByOrderId(paymentStatus,movieInves.getId()  ,request.getOrderId());
            paymentTransection.setStatus(paymentStatus); 
            paymentTransection.setInvestmentId(movieInves.getId());
            paymentTransection.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            paymentTransection.setPaymentId(request.getPaymentId());
            paymentTransactionRepository.save(paymentTransection)  ;          
            
        	 
         }
	   else if(paymentTransection.getStatus().equalsIgnoreCase(PayoutStatus.PAID.name()))
	   {
	   logger.info(" Payment is already paid ");
	   
	     // Ignore duplicate webhook/Android callback

	   }
	   else if (paymentTransection.getStatus().equalsIgnoreCase(PayoutStatus.PENDING.name()) &&
		        paymentStatus.equalsIgnoreCase("FAILED")) {
		    paymentTransection.setStatus(paymentStatus);
		    paymentTransection.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		    paymentTransactionRepository.save(paymentTransection);
		    logger.warn("Payment marked as FAILED for order: {}", request.getOrderId());
		    
			aduit.logActionNoTrx(0, ActionType.CRITICAL.name(),
					EntityName.PAYMENT_FAILURE.name(),0,
					"Payment failed  " + request.getOrderId()  + ":" +request.getPaymentId(),
					request);
			
			

		}
	   else if (paymentTransection.getStatus().equalsIgnoreCase(PayoutStatus.PAID.name()) &&
		        paymentStatus.equalsIgnoreCase("FAILED")) {
		    logger.warn("Payment marked as PAID alerady  for order: {}", request.getOrderId()  + " But geting Failed :" + paymentStatus);

		
	   }
	   
	   else {
		    logger.warn("Received unexpected payment status '{}' for order: {}", paymentStatus, request.getOrderId());
		}
	   		
		
	}

	
	@Transactional
	@Override
	public CommonResponse getBuyShares(MovieInvestRequest movieInvRequest, String phoneNumber) {
		// TODO Auto-generated method stub
		
		logger.info(
				"User trying to invest in movie " + phoneNumber + " : " + UserContextHolder.getContext().getPhone());
		
		UserContext  loginUser = UserContextHolder.getContext();
		
		if(loginUser == null)
			throw new RuntimeException("unable to find login uses please relogin ");
		
		logger.info(" User id " + loginUser.getUserId()  );
		
		
		
		
		phoneNumber = UserContextHolder.getContext().getPhone();
		
		if(movieInvRequest.getNumberOfShares() != movieInvRequest.getExistTotalShare())
		{
			  throw new RuntimeException("Share availability has changed since your last calculation. Please recheck the updated total shares before confirming your purchase.");
			  
		}
		
	
	           
	
		    
		Optional<Movie> movieOpt = movieRepository.findById(Long.valueOf(movieInvRequest.getMovieId()));
		if (movieOpt.isPresent()) {
			Movie movie = movieOpt.get();
		//	int originalAmount = movie.getBudget();
			
			MovieShareType   movShareType = movShareTyeRepo.findById(movieInvRequest.getShareTypeId())
					                             .orElseThrow(() -> new RuntimeException("Movie Share Type is not found "));
			
			
			
			
			
			int originalAmount = movShareType.getNumberOfShares()  * movie.getPerShareAmount();
			int perShareAmount = movie.getPerShareAmount();
			//int investedAmount = movieInvestRepo.getInvestedAmountByMovieIdAndShareTypeId(movieInvRequest.getMovieId(),movieInvRequest.getShareTypeId());
			int investedAmount  = 0;
			int newInvestedAmout = investedAmount + (perShareAmount * movieInvRequest.getNumberOfShares());
			
			BigDecimal maxInvestAmount = new BigDecimal(configCacheService.getConstant("max_invest_amount"));
			
			BigDecimal userAlreadyInvested = movieInvestRepo.getTotalInvestedAmountCurrentYear(loginUser.getUserId());
			if (userAlreadyInvested == null) {
			    userAlreadyInvested = BigDecimal.ZERO;
			}
			
			BigDecimal perShareAmountBD = new BigDecimal(String.valueOf(perShareAmount));
			BigDecimal totalAmountToInvest = perShareAmountBD.multiply(
			    BigDecimal.valueOf(movieInvRequest.getNumberOfShares())
			);		
			
			logger.info("Invested Report : already user "+ loginUser.getUserId()  +" already  invested "
					+ userAlreadyInvested  + " current share amount " + totalAmountToInvest
					+ ":  max invest limit : "+ maxInvestAmount  );
			
			userAlreadyInvested = userAlreadyInvested.add(totalAmountToInvest);
			
			

			
				if (userAlreadyInvested.compareTo(maxInvestAmount) > 0) {
				    throw new RuntimeException("User investing amount " + userAlreadyInvested 
				        + " cannot be greater than " + maxInvestAmount);
				}		
		
				logger.info(" Movie id "+movieInvRequest.getMovieId() + " : share Type id : "+movieInvRequest.getShareTypeId() );
				logger.info(" invested amount on shareType (original: " + originalAmount + " :already invested : " + investedAmount + " new invested amount " + newInvestedAmout);	
				
			
	
			// checking movie reached budget amount or not
			if (newInvestedAmout > originalAmount) {
				logger.info("user can not buy share by userId " + phoneNumber);

				int remainAmount = originalAmount - investedAmount;
				int buyableShares = 0;
				if (remainAmount >= perShareAmount)
					buyableShares = remainAmount / perShareAmount;
				return CommonResponse.builder().status(Messages.STATUS_FAILURE)
						.message(" Buyable share :" + buyableShares + " Buyable Amount :" + remainAmount).build();

			} else {
				logger.info("user can buy share by userId " + phoneNumber);
				Optional<User> optUser = userRepo.findByphoneNumber(phoneNumber);

				if (optUser.isPresent()) {
					User user = optUser.get();

					if (user.getStatus().equalsIgnoreCase(StatusEnum.INACTIVE.name()))
						return CommonResponse.builder().status(Messages.STATUS_FAILURE)
								.message(Messages.USER_NOT_ACTIVE).build();
					else {
						logger.info("user buying share " + user.getPhoneNumber() + " perShareAmount " + perShareAmount);

						int userInvest = perShareAmount * movieInvRequest.getNumberOfShares();

						logger.info("amount " + userInvest + " shares" + movieInvRequest.getNumberOfShares());
						Optional<MovieInvestment> movieInvesOpt = movieInvestRepo.findByUserIdAndMovieIdAndShareTypeId(
								user.getId(), movieInvRequest.getMovieId(), movieInvRequest.getShareTypeId());

						
						
						
						if (movieInvesOpt.isPresent()) {
							logger.info("user buying share have already buyed  " + user.getPhoneNumber() + " : "
									+ movieInvRequest.getMovieId());

							MovieInvestment movieInves = movieInvesOpt.get();

							
							
							logger.info("Movie OFfer verfication starts ");
							
										 
							aduit.logAction(UserContextHolder.getContext().getUserId(), ActionType.UPDATE.name(),
									EntityName.MOVIE_INVESTMENT.name(), movieInves.getId(),
									"new investment is added" + UserContextHolder.getContext().getPhone(),
									movieInvRequest);

							
							
							
							movieInves.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
							MovieIinvestSuccess invesSucces = MovieIinvestSuccess.builder()
									.movieId(movieInvRequest.getMovieId())
									.numberOfNewShares(movieInvRequest.getNumberOfShares())
									//..perShareAmount(movie.getPerShareAmount())
									.numberOfOldShares(movieInves.getNumberOfShares())
									.oldShareAmount(movieInves.getAmountInvested()).build();
							
							
							//movieInves.setNumberOfShares(
								//	movieInves.getNumberOfShares() + movieInvRequest.getNumberOfShares());
							BigDecimal amountInvested = movieInves.getAmountInvested()
									.add(BigDecimal.valueOf(userInvest));
						
							
							
						//	movieInves = addOfferAmountToUser(offerMoneyResponse,movieInves,movieInvRequest,perShareAmount,user.getId(),true);

							movieInvestRepo.save(movieInves);

							return CommonResponse.builder().status(Messages.STATUS_SUCCESS)
									.message("New and Old Share merged successfully").result(invesSucces).build();
						} else {
							logger.info("user buying share newly  " + user.getPhoneNumber() + " : "
									+ movieInvRequest.getMovieId());
							
						
							
							BigDecimal amountInvested = BigDecimal.valueOf(userInvest);
							MovieInvestment movieInves = new MovieInvestment();
							logger.info("Nees dsfasfsdf s " + movieInves.getId());;

														
							
							

							movieInves.setAmountInvested(amountInvested);
							movieInves.setMovieId(movieInvRequest.getMovieId());
							//movieInves.setNumberOfShares(movieInvRequest.getNumberOfShares());
							movieInves.setUserId(Integer.parseInt(user.getId() + ""));
							movieInves.setShareTypeId(movieInvRequest.getShareTypeId());
							movieInves.setStatus(StatusEnum.ACTIVE.name().toLowerCase());
							movieInves = movieInvestRepo.save(movieInves);
							
							
							
							MovieIinvestSuccess invesSucces = MovieIinvestSuccess.builder()
									.movieId(movieInvRequest.getMovieId())
									.numberOfNewShares(movieInvRequest.getNumberOfShares())
									//.perShareAmount(movie.getPerShareAmount()).numberOfOldShares(0)
									.oldShareAmount(BigDecimal.valueOf(0)).build();
							System.out.println(
									"use id :" + UserContextHolder.getContext().getUserId() + " : " + user.getId());
                         							
							movieInves = movieInvestRepo.save(movieInves);
							//movieInves = addOfferAmountToUser(offerMoneyResponse,movieInves,movieInvRequest,perShareAmount,user.getId(),false);
							movieInves = movieInvestRepo.save(movieInves);
							
							

							aduit.logAction(UserContextHolder.getContext().getUserId(), ActionType.CREATE.name(),
									EntityName.MOVIE_INVESTMENT.name(), movieInves.getId(),
									"New investemnt is added " + UserContextHolder.getContext().getPhone(),
									movieInvRequest);

							return CommonResponse.builder().status(Messages.STATUS_SUCCESS)
									.message("New Share buyed successfully").result(invesSucces).build();

						}
					}

				} else
					return CommonResponse.builder().status(Messages.STATUS_FAILURE)
							.message(Messages.REG_PHONE_NUMBER_NOT_FOUND).build();

			}

		} else {
			return CommonResponse.builder().status(Messages.STATUS_FAILURE).message("Movie is not found by given Id")
					.build();
		}

	}
	
	
	
	//  update for testing service
	@Transactional
	@Override
	public void updatePaymentStatusTesting(PaymentUpdateRequest request) {
		// TODO Auto-generated method stub
		logger.info("*****update payment status stars *****");
		logger.info("** input orderId ****" + request.getOrderId() + "* : payment id :*" + request.getPaymentId()
		            + " :PaymentMethod :" + request.getPaymentMethod()
		         + " :Singnature :" +request.getSignature()    
				);
		
		if (request.getSignature() == null || request.getSignature().isEmpty()) {
		    logger.info("Missing signature. Cannot verify payment.");
		    return;
		}
		
		 PaymentService  paymentService = paymentFactory.getPaymentService();
 /*         boolean  isPaymentVerified = paymentService.verifyPayment(request.getPaymentId(),
        		 request.getSignature(), request.getOrderId());
         
        
          
         logger.info("isPaymentVerified :" +  isPaymentVerified); 
          
         if(!isPaymentVerified)
         {
        	    logger.warn("❌ Missing signature for orderId: {}. Stopping payment verification.", request.getOrderId());
        	 aduit.logActionNoTrx(0, ActionType.CRITICAL.name(),
 					EntityName.PAYMENT_FAILURE.name(),0,
 					"Payment signature is  triying to compramise " + request.getOrderId()  + ":" +request.getPaymentId(),
 					request);

        	 
        	 return;
         }
           */
        
         
        String paymentStatus = PayoutStatus.PAID.name();
        
       
         
         
		MoviePaymentTransaction paymentTransection = paymentTransactionRepository.findByOrderId(request.getOrderId());
		
		
		
		if(paymentTransection == null)
		{
			logger.info("**Payment trasection is not found for " + request.getOrderId());
			//throw new RuntimeException("Transection is  not found");
			return ;
		}
		
	   logger.info("paymentTransection check status " +paymentTransection.getStatus());
	
	   boolean isPendingTrxPaid = paymentTransection.getStatus().equalsIgnoreCase(PayoutStatus.PENDING.name()) &&
			    (paymentStatus.equalsIgnoreCase("PAID") || paymentStatus.equalsIgnoreCase("CAPTURED")) ;
		
	   if (isPendingTrxPaid)
	   {
        	 logger.info("Started updating payment details in investment ");
        	
        	 int movieId = paymentTransection.getMovieId();
        	 int userId = paymentTransection.getUserId();
        	 int shareTypeId = paymentTransection.getShareTypeId();
        	 
        	 
        	 logger.info("stated reading movie information");
				Optional<MovieInvestment> movieInvesOpt = movieInvestRepo.findByUserIdAndMovieIdAndShareTypeId(
						userId,movieId, shareTypeId);
        	 
				MovieInvestment movieInves  = new MovieInvestment();
				
				if(movieInvesOpt.isPresent())
				{
					movieInves =  movieInvesOpt.get();     	 

				}
				else
				{
					movieInves.setInvestedAt(new Timestamp(System.currentTimeMillis()));
					movieInves.setMovieId(movieId);	
					movieInves.setShareTypeId(shareTypeId);
					movieInves.setStatus(StatusEnum.ACTIVE.name());
					movieInves.setUserId(paymentTransection.getUserId());
			    }
        	           
				 logger.info("stated reading movie information");
					
				aduit.logAction(0, ActionType.UPDATE.name(),
						EntityName.MOVIE_INVESTMENT.name(), movieInves.getId(),
						"new investment is added" ,
						movieInves);
				
				//   	movieInves.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
				

				
				MovieIinvestSuccess invesSucces = MovieIinvestSuccess.builder()
				.movieId(movieInves.getMovieId())
				.numberOfNewShares(paymentTransection.getTotalShare() )
				.perShareAmount(paymentTransection.getPerShareAmount())
				.numberOfOldShares(movieInves.getNumberOfShares())
				.oldShareAmount(movieInves.getAmountInvested()).build();
	
        	 
        	  int newShare = movieInves.getNumberOfShares() +  paymentTransection.getTotalShare();
        	  movieInves.setNumberOfShares(newShare);
        	  movieInves.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        	  movieInves.setAmountInvested(movieInves.getAmountInvested().add(paymentTransection.getAmount()));
        	  movieInves = movieInvestRepo.save(movieInves);

              logger.info("Calculating Wallet offer Amount to be send ......");        	  
              BigDecimal offerWalletMoney = investMoneyRepo.getTotalWalletAmountByOrderId(paymentTransection.getOrderId());  
              logger.info("Walled discount is found " + offerWalletMoney);
              if (offerWalletMoney != null && offerWalletMoney.compareTo(BigDecimal.ZERO) > 0) {
            	    // Wallet money is greater than zero
            	  logger.info("Entering into wallet credit " + userId + " : amount :" + offerWalletMoney);
               userWalletService.addShareCash(userId,offerWalletMoney);

            	}
              
              
            logger.info("savving into PaymentTransection and invest_money_offer"); 
            investMoneyRepo.updateStatusByOrderId(paymentStatus,movieInves.getId()  ,request.getOrderId());
            paymentTransection.setStatus(paymentStatus); 
            paymentTransection.setInvestmentId(movieInves.getId());
            paymentTransection.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            paymentTransection.setPaymentId(request.getPaymentId());
            paymentTransactionRepository.save(paymentTransection)  ;          
            
        	 
         }
	   else if(paymentTransection.getStatus().equalsIgnoreCase(PayoutStatus.PAID.name()))
	   {
	   logger.info(" Payment is already paid ");
	   
	     // Ignore duplicate webhook/Android callback

	   }
	   else if (paymentTransection.getStatus().equalsIgnoreCase(PayoutStatus.PENDING.name()) &&
		        paymentStatus.equalsIgnoreCase("FAILED")) {
		    paymentTransection.setStatus(paymentStatus);
		    paymentTransection.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		    paymentTransactionRepository.save(paymentTransection);
		    logger.warn("Payment marked as FAILED for order: {}", request.getOrderId());
		    
			aduit.logActionNoTrx(0, ActionType.CRITICAL.name(),
					EntityName.PAYMENT_FAILURE.name(),0,
					"Payment failed  " + request.getOrderId()  + ":" +request.getPaymentId(),
					request);
			
			

		}
	   else if (paymentTransection.getStatus().equalsIgnoreCase(PayoutStatus.PAID.name()) &&
		        paymentStatus.equalsIgnoreCase("FAILED")) {
		    logger.warn("Payment marked as PAID alerady  for order: {}", request.getOrderId()  + " But geting Failed :" + paymentStatus);

		
	   }
	   
	   else {
		    logger.warn("Received unexpected payment status '{}' for order: {}", paymentStatus, request.getOrderId());
		}
	   		
		
	}

	@Override
	public List<UserMoviePurchaseProjection> getUserMoviePurchasesByUserId() {
		// TODO Auto-generated method stub
		UserContext loginUser = UserContextHolder.getContext();
		if(loginUser == null)
			throw new RuntimeException("User is not found");
		logger.info("current user id " +  loginUser.getUserId());
		
		return movieInvestRepo.findUserMoviePurchases(loginUser.getUserId());
	}

	@Override
	public UserShareOfferListResponse getUserShareWithOffer(int movieId) {
		// TODO Auto-generated method stub
		UserContext loginUser = UserContextHolder.getContext();
		if(loginUser == null)
			throw new RuntimeException("Uesr is not found");
		UserShareOfferListResponse userResponse = UserShareOfferListResponse.builder().build();		
		MovieDTO movie = movieRepository.findMoviesWithStatusAndTypeAndLangName(movieId);
		if(movie == null)
			throw new RuntimeException("Movie is not found ");
		
		userResponse.setMovie(movie);
		loginUser.setUserId(29);
		Optional<List<UserInvestmentSharTypeDTO>>  shareList =  movieInvestRepo.getInvestmentsWithShareTypeByMovId(movieId
				, loginUser.getUserId());
		List<ShareOfferListResponse> shareOfferList = new ArrayList<ShareOfferListResponse>();

		if(shareList.isPresent())
		{
			logger.info("share list is found");
			List<UserInvestmentSharTypeDTO> userShareList = shareList.get();
			logger.info("share list is found "  + userShareList.size());

			if(userShareList != null )
			{
				for(UserInvestmentSharTypeDTO userShare :userShareList )
				{
					logger.info("share list is found");

					ShareOfferListResponse shareOffer = ShareOfferListResponse.builder().build();
					shareOffer.setShareDetail(userShare);
					
					List<InvestOfferMoney> investMoneyList = investMoneyRepo.findByMovieIdAndShareTypeIdAndUserId(movieId,userShare.getShareId(),loginUser.getUserId());
					
					shareOffer.setInvestMoneyList(investMoneyList);
					shareOfferList.add(shareOffer);
					
				}
			}
		}
		userResponse.setShareOffer(shareOfferList);		
		return userResponse;
	}
	
	
}
