package com.riseup.flimbit.gateway.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.gateway.pan.SurePassPanVerificationService;
import com.riseup.flimbit.service.SystemSettingsService;

import java.util.Objects;

@Component
public class PaymentFactory {

    @Autowired
    private RazorpayPaymentService razorpayService;

    @Autowired
    private PayUPaymentService payUService;

    @Autowired
    private SystemSettingsService systemSettingsService;

    @Value("${default_active_payment_gateway}")
    String defaultProvider;
    
    Logger logger = LoggerFactory.getLogger(PaymentFactory.class);


    public PaymentService getPaymentService() {
        String activeProvider = Objects.requireNonNullElse(
            systemSettingsService.getValue("default_active_payment_gateway", EntityName.PAYMENT.name()),
            defaultProvider
        );
        
        logger.info("Payment Factory starts (active Payement )"  + activeProvider);
        logger.info("text "+  systemSettingsService.getValue("default_active_payment_gateway", EntityName.PAYMENT.name()) );
     
        if ("PAYU".equalsIgnoreCase(activeProvider)) {
            return payUService;
        } else {
            return razorpayService; // Default
        }
    }
}
