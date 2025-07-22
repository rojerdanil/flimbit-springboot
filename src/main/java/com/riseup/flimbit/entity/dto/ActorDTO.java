package com.riseup.flimbit.entity.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

public interface ActorDTO {
	
	    Integer getId();

	    String getName();

	    String getGender();

	    String getImageUrl();

	    Integer getPopularityScore();

	    Integer getAwardsCount();

	    int getRole();

	    int getLanguage();

	    String getLanguageName();
	    
	    String getRoleName();
}
