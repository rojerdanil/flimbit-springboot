package com.riseup.flimbit.constant;

public enum PromotionTypeEnum {

	WELCOME("Promo for new user signup"),
    REFERRAL("Promo for referring a new user"),
    EVENT("Promo for special occasions/festivals");

    private final String description;

    PromotionTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
	
}
