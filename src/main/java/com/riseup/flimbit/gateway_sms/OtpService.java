package com.riseup.flimbit.gateway_sms;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.constant.StatusEnum;
import com.riseup.flimbit.entity.SystemSettings;
import com.riseup.flimbit.repository.SystemSettingsRepository;
import com.riseup.flimbit.serviceImp.MovieInvestServiceImp;

@Service
public class OtpService {
	
	
	Logger logger = LoggerFactory.getLogger(OtpService.class);

	
	@Autowired
	SystemSettingsRepository  systemRepository;
	
    @Value("${default_active_sms_gateway}")
    String defaultProvider;

    private final SmsGatewayProvider smsGatewayProvider;

    public OtpService(SmsGatewayProvider smsGatewayProvider) {
        this.smsGatewayProvider = smsGatewayProvider;
    }

    public OtpResponse  sendOtp(String phoneNumber, String otp) {
     logger.info("Starts otp send processs for  "  + phoneNumber);
    	
    	
        String activeProvider = getActiveSmsProviderFromDB(); // e.g., "twilio"
        SmsGatewayService smsService = smsGatewayProvider.getSmsService(activeProvider);
        
        return smsService.sendOtp(phoneNumber, otp);
    }
    
    public OtpResponse  verifyOtp(String session, String otp) {
        logger.info("Starts verifyOtp send processs for  "  + session);
       	
       	
           String activeProvider = getActiveSmsProviderFromDB(); // e.g., "twilio"
           SmsGatewayService smsService = smsGatewayProvider.getSmsService(activeProvider);
           return smsService.verifyOtp(session, otp);
       }
       
    

    private String getActiveSmsProviderFromDB() {
    
    	 Optional<SystemSettings> sysSettingOPt   =	systemRepository.findByKeyIgnoreCaseAndGroupNameIgnoreCase("active_sms_gateway", EntityName.SMS.name());
		 String activeProvider =  sysSettingOPt.isPresent() ?   sysSettingOPt.get().getValue()  :  defaultProvider;
		 logger.info("Selecting active activeProvider " + activeProvider);		                                        

        return activeProvider; // later fetch from system settings table
    }
}
