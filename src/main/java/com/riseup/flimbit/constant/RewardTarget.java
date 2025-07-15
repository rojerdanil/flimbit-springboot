package com.riseup.flimbit.constant;

public enum RewardTarget {
	    REFERRER("Referrer"),
	    REFERRED("Referred"),
	    INFLUENCER("Influencer"),
	    SELF("Self"),
	    FRIEND("Friend"),
	    BOTH("Both");
	
	
	private final String label;

    RewardTarget(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
	
	
}