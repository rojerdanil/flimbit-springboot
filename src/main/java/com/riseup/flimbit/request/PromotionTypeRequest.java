package com.riseup.flimbit.request;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PromotionTypeRequest {
	
	 Long id;
     String typeCode;
    String description;
     String status ;  // Default is active    
    int userCount;
    int expiryDays;
    boolean edit;   
    


}
