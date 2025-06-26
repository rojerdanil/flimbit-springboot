package com.riseup.flimbit.entity;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "languages")
@Getter
@Setter
public class Language {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "languages_id_seq")
	@SequenceGenerator(name = "languages_id_seq", sequenceName = "languages_id_seq", allocationSize = 1)

    private Long id;

    private String name;
    private String nativeScript;
    private String region;
    private String status;

}
