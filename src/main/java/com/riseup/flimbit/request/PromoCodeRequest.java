package com.riseup.flimbit.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromoCodeRequest {
	
	String promoCode;
	String  promoType;
	String status;
    int id;
}
