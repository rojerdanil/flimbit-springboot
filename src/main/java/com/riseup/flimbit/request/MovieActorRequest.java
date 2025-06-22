package com.riseup.flimbit.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieActorRequest {

	private Integer id;

	private int movieId;

	private int roleMovieId;
	private int actorId;

}
