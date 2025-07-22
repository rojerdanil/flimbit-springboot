package com.riseup.flimbit.entity;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "system_settings")
public class SystemSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String key;

    @Column(nullable = false)
    private String value;

    private String description;

    private String type;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "updated_at", updatable = false)
    private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_by")
    private Integer updatedBy;


}
