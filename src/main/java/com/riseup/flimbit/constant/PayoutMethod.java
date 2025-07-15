package com.riseup.flimbit.constant;

public enum PayoutMethod {
    WALLET("Wallet"),
    BANK_TRANSFER("Bank Transfer"),
    UPI("UPI");

    private final String methodName;

    // Constructor to associate the string value
    PayoutMethod(String methodName) {
        this.methodName = methodName;
    }

    // Getter for the methodName
    public String getMethodName() {
        return methodName;
    }

    // Optional: Method to get enum from String
   
}
