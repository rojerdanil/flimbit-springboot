package com.riseup.flimbit.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OfferTypeRequest {
     String name;
     String description;


}
