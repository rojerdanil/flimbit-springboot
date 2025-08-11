package com.riseup.flimbit.gateway.payment;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class PayUPaymentService implements PaymentService {
    @Override
    public String createOrder(String orderId, BigDecimal amount, String currency,Map<String, Object> notes) {
        // Future implementation for PayU
        return "PAYU_ORDER_" + orderId;
    }

    @Override
    public boolean verifyPayment(String paymentId, String signature, String orderId) {
        return true; // Simulated
    }

	@Override
	public PaymentOrderResponse getOrder(String gatewayOrderId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}

