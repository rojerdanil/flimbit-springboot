package com.riseup.flimbit.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayAllShareReturnRequest {

	int movieId;
	int userId;
	int amount;
	String method;
	String status;
	int shareTypeId;
}
