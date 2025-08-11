package com.riseup.flimbit.gateway.payment;
import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentOrderResponse {
    private String orderId;          // Gateway order ID
    private String internalOrderRef; // Your internal reference ID
    private String status;           // CREATED, PAID, FAILED
    private BigDecimal amount;
    private BigDecimal amountPaid;
    private String currency;
    private String provider;         // RAZORPAY, PAYPAL, STRIPE
}
