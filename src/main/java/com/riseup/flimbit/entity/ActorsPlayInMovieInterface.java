package com.riseup.flimbit.entity;

import jakarta.persistence.Column;

public interface ActorsPlayInMovieInterface {
	
	   int getId();
	     int getMovieId();
	     int getMovieRoleId();
	     int getMovieActorId();
	     String getActorName();
	     String getRoleName();

}
