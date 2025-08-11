package com.riseup.flimbit.gateway_sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsGatewayProvider {
	
	 @Autowired
	    private TwoFactorSmsGatewayService twoFactorSmsGatewayService;

	    @Autowired
	    private TwilioSmsGatewayService twilioSmsGatewayService;

    public SmsGatewayService getSmsService(String providerName) {
        if ("2factor".equalsIgnoreCase(providerName)) {
            return twoFactorSmsGatewayService;
        } 
        else if ("twilio".equalsIgnoreCase(providerName)) {
            return twilioSmsGatewayService;
        } 
        else {
            throw new RuntimeException("No SMS Provider found for: " + providerName);
        }
    }
}
