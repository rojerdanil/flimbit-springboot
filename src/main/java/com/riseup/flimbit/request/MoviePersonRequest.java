package com.riseup.flimbit.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MoviePersonRequest {
	    private Integer id;
	    private String name;
	    private String gender;
        private String imageUrl;
        private Integer popularityScore;
	    private Integer awardsCount;
	    int role;
	    int language;


}
