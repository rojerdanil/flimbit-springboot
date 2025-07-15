package com.riseup.flimbit.constant;

public enum RewardType {
    CASH("Cash"), 
    SHARE("Share"),
    PERCENTAGE("Percentage"),
    DISCOUNT("Discount"),
    BONUS("Bonus");
	
	private final String label;

    RewardType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    
}