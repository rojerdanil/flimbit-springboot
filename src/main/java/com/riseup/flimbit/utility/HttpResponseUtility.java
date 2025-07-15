package com.riseup.flimbit.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.response.CommonResponse;

public class HttpResponseUtility {

	public static ResponseEntity<?> getHttpSuccess(Object obj)
	{
	if(obj != null)	
	return  ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_SUCCESS).result(	
    		obj).build());
	else
		return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder().status(Messages.STATUS_FAILURE).message(Messages.STATUS_FAILURE).build());
	}
	public static ResponseEntity<?> getHttpError(String message,Object obj)
	{
		String msg = Messages.STATUS_FAILURE;
		if(message !=null && message.trim().length() > 0)
			msg = message;
		
	return  ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder().status(Messages.STATUS_FAILURE).message(msg).result(	
    		obj).build());
	}
	
}
