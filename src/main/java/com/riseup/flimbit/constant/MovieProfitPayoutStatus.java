package com.riseup.flimbit.constant;

public enum MovieProfitPayoutStatus {

    INITIATED,     // Payout request created, awaiting processing
    APPROVED,      // Payout approved and queued for processing
    ON_HOLD,       // Payout temporarily paused, awaiting manual action
    REJECTED,      // Payout request rejected due to validation or policy
    FAILED,        // Payment attempt failed during processing
    COMPLETED,      // Payment successfully completed
    VERIFIED,
    SYSTEM,
    READY_FOR_PAYMENT,
    PARTIALLY_COMPLETED,
    IN_PROGRESS,
    PAYMENT_PARTIALLY_COMPLETED,
    PAYMENT_IN_PROGRESS,
    PAYMENT_COMPLETED,
    PAYMENT_FULLY_FAILED
    
    
}