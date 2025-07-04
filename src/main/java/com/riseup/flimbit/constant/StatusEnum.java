package com.riseup.flimbit.constant;

public enum StatusEnum {
	
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    EXPIRED("Expired");

    private final String description;

    StatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}
