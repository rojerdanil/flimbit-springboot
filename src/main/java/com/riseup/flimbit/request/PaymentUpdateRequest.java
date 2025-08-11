package com.riseup.flimbit.request;

import lombok.Data;

@Data
public class PaymentUpdateRequest {
	private String orderId;       // Razorpay order ID
    private String paymentId;     // Razorpay payment ID
    private String paymentMethod; // Selected payment method (UPI, CARD, NETBANKING)
    private String signature;     // Razorpay-generated signature for verification
    private String errorMsg ;
}
