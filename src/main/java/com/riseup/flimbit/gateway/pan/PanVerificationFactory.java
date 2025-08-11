package com.riseup.flimbit.gateway.pan;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.gateway_sms.TwoFactorSmsGatewayService;
import com.riseup.flimbit.service.SystemSettingsService;

@Service
public class PanVerificationFactory {

    @Autowired
    private SurePassPanVerificationService surePassService;

    @Autowired
    private KarzaPanVerificationService karzaService;
    
    Logger logger = LoggerFactory.getLogger(PanVerificationFactory.class);

	  @Autowired
	    private SystemSettingsService systemSettingsService;
    
    
    @Value("${default_active_pan_gateway}")
    String defaultProvider;



    public PanVerificationService getPanService() {
    	String activeProvider = Objects.requireNonNullElse(
		        systemSettingsService.getValue("default_active_pan_gateway", EntityName.PAN.name()),
		        defaultProvider
		);		
	 logger.info("PanVerificationService starts ");

	 logger.info(" Default Active provider "  + activeProvider);
	 
    	
        if ("Karza".equalsIgnoreCase(activeProvider)) {
            return karzaService;
        } else {
            return surePassService; // Default
        }
    }
}
