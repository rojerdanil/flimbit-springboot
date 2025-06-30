package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "share_category")
@Getter
@Setter
public class ShareCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "share_category_seq_gen")
    @SequenceGenerator(name = "share_category_seq_gen", sequenceName = "share_category_seq", allocationSize = 1)
    private int id;

    @Column(nullable = false)
    private String name;

    private String description;

    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime updatedDate = LocalDateTime.now();
}
