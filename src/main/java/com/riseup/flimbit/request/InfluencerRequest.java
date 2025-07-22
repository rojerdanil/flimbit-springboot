package com.riseup.flimbit.request;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Getter
@Setter
public class InfluencerRequest {
	

	private String firstName;

	private String lastName;

	private String email;

	private String phoneNumber;

	private String socialMediaHandle;

    String status;
	
	int languageId;
	
	String promoCode;
	
}
