package com.riseup.flimbit.request;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class DeleteRequest {
	
	List<Integer> idsList;
	

}
