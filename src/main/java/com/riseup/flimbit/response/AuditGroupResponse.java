package com.riseup.flimbit.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AuditGroupResponse {
	
	List<KeyValueResponse> entityGroup;
	List<KeyValueResponse> actionGroup;


}
