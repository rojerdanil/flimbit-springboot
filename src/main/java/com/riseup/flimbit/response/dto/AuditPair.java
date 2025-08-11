package com.riseup.flimbit.response.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AuditPair {
	
	Object sourcne;
	Object destination;

}
