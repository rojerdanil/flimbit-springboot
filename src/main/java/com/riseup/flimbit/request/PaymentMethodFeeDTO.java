package com.riseup.flimbit.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentMethodFeeDTO {
	
    private String method;
    private BigDecimal feePercentage;

}
