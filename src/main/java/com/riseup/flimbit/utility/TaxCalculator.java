package com.riseup.flimbit.utility;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.response.PaymentFeeResponse;
import com.riseup.flimbit.service.SystemSettingsService;

@Service
public class TaxCalculator {
	
    @Autowired
    private SystemSettingsService systemSettingsService;

	 public PaymentFeeResponse getPaymentTax(BigDecimal amount , String method)
	 {
		 
		 BigDecimal gstRate = getBigDecimalSetting("gateway_gst_rate", BigDecimal.valueOf(0.18));

		   
		    
		    BigDecimal feeRate;
		    switch (method.toUpperCase()) {
		        case "UPI":
		            feeRate = getBigDecimalSetting("gateway_fee_upi", BigDecimal.ZERO);
		            break;
		        case "CARD":
		            feeRate = getBigDecimalSetting("gateway_fee_card", BigDecimal.valueOf(0.02));
		            break;
		        case "NETBANKING":
		            feeRate = getBigDecimalSetting("gateway_fee_netbanking", BigDecimal.valueOf(0.02));
		            break;
		        default:
		            feeRate = getBigDecimalSetting("gateway_fee_default", BigDecimal.valueOf(0.02));
		    }

		    // Calculate fee and GST
		    BigDecimal fee = amount.multiply(feeRate).setScale(2, BigDecimal.ROUND_HALF_UP);
		    BigDecimal gst = fee.multiply(gstRate).setScale(2, BigDecimal.ROUND_HALF_UP);
		    BigDecimal totalPayable = amount.add(fee).add(gst).setScale(2, BigDecimal.ROUND_HALF_UP);

		    // Build and return response
		    return PaymentFeeResponse.builder()
		            .investmentAmount(amount)
		            .convenienceFee(fee)
		            .gst(gst)
		            .totalPayable(totalPayable)
		            .paymentMethod(method.toUpperCase())
		            .build();

	 }
	 
	 public BigDecimal getBigDecimalSetting(String key, BigDecimal defaultValue) {
		    String value = systemSettingsService.getValue(key, EntityName.PAYMENT.name());
		    if (value != null && !value.trim().isEmpty()) {
		        try {
		            return new BigDecimal(value.trim());
		        } catch (NumberFormatException e) {
		            return defaultValue;
		        }
		    }
		    return defaultValue;
		}
}
