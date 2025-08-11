package com.riseup.flimbit.gateway.payment;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {
    String createOrder(String orderId, BigDecimal amount, String currency,Map<String, Object> notes) throws Exception;
    boolean verifyPayment(String paymentId, String signature, String orderId);
    PaymentOrderResponse getOrder(String gatewayOrderId) throws Exception;

}

