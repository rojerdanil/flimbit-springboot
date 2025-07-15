package com.riseup.flimbit.constant;

public enum PayoutStatus {
    PENDING,       // Awaiting payment processing
    PAID,          // Payment completed successfully
    FAILED,        // Payment attempt failed
    CANCELLED,     // Payment process was cancelled
    PROCESSING,    // Payment is being processed
    COMPLETED,     // Payment successfully completed
    REFUNDED,      // Payment was refunded to the user
    AWAITING_CONFIRMATION, // Waiting for external confirmation
    UNDER_REVIEW,  // Payment is being reviewed
    ON_HOLD,       // Payment on hold due to issues
    IN_PROGRESS;   // Payment is in progress
}
	