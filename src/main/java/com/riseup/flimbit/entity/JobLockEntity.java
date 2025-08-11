package com.riseup.flimbit.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_lock")
@Getter
@Setter
public class JobLockEntity {

    @Id
    private String jobName;
    private LocalDateTime lockedAt;
    @Column(name = "locked", nullable = false)
    private boolean locked;

  
}

