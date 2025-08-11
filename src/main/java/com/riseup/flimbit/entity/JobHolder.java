package com.riseup.flimbit.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "job_holder")
@Getter
@Setter
@NoArgsConstructor       // <-- important for JPA
@AllArgsConstructor
@Builder
public class JobHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "movie_id", nullable = false)
    private Integer movieId;

    @Column(name = "job_name", nullable = false, length = 255)
    private String jobName;

    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    @Column(name = "job_started_date")
    private Timestamp jobStartedDate;

    @Column(name = "job_end_date")
    private Timestamp jobEndDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

}
