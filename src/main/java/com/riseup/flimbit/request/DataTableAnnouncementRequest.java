package com.riseup.flimbit.request;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class DataTableAnnouncementRequest {
	
	
	 private int draw;
	    private int start;
	    private int length;
	    private String sortColumn;
	    private String sortOrder;
	    private String searchText;
	    private String language;
	    private String status;

}
