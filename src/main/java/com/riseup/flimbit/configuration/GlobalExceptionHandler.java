package com.riseup.flimbit.configuration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.response.CommonResponse;



@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponse> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.OK).body( CommonResponse.builder().status(Messages.STATUS_FAILURE)
				.message(ex.getMessage()).build());
        		
        		
    }

}
