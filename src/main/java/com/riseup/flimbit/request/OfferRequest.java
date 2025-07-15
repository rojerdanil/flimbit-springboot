package com.riseup.flimbit.request;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferRequest {
	
    String offerName;
     int  offerType;
     String targetAudience;
     String status ;

}
