package com.riseup.flimbit.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Getter
@Setter
public class MovieActorRequest {

	private int id;

	private int movieId;

	private int roleMovieId;
	private int actorId;

}
