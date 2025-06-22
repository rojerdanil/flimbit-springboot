package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "movie_status")
@Getter
@Setter
public class MovieStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movie_status_seq")
	@SequenceGenerator(name = "movie_status_seq", sequenceName = "movie_status_seq", allocationSize = 1)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    
}
