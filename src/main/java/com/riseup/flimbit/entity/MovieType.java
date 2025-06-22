package com.riseup.flimbit.entity;


import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "movie_types")
@Getter
@Setter
public class MovieType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movie_types_seq")
    @SequenceGenerator(name = "movie_types_seq", sequenceName = "movie_types_id_seq", allocationSize = 1)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    // Constructors
}

