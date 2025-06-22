package com.riseup.flimbit.entity;


import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "movie_person")
@Getter
@Setter
public class MoviePerson {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "person_seq")
    @SequenceGenerator(name = "person_seq", sequenceName = "person_id_seq", allocationSize = 1)
    private Integer id;

    private String name;
    private String gender;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "popularity_score")
    private Integer popularityScore;

    @Column(name = "awards_count")
    private Integer awardsCount;
    int role;
    int language;
}
