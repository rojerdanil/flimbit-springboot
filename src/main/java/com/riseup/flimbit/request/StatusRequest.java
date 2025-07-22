package com.riseup.flimbit.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StatusRequest {
	
	int id;
	String status;
	String description;

}
