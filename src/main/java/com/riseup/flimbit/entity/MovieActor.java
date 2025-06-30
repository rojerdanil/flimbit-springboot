package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "movie_actors")
@Getter
@Setter
public class MovieActor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movie_actor_seq")
    @SequenceGenerator(name = "movie_actor_seq", sequenceName = "movie_actors_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "movie_id")
    private int movieId;

    @Column(name = "movie_role_id")
    private int roleMovieId;
    @Column(name = "movie_actor_id")
    private int actorId;
}
