package com.riseup.flimbit.entity.dto;

public interface ChartProjectionDTO {
	
	String getMonth();               // from TO_CHAR(create_date, 'Mon')
    Integer getTotalInvestment();  

}
