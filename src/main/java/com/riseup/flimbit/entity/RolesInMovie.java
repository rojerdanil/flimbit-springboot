package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles_in_movie")
@Getter
@Setter
public class RolesInMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_movie_seq")
    @SequenceGenerator(name = "role_movie_seq", sequenceName = "role_movie_id_seq", allocationSize = 1)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}
