package com.riseup.flimbit.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PanRequest {
	String panNumber;
	

    private String nameAsPerPan;

    private String dateOfBirth; // format: dd/MM/yyyy

   
}
