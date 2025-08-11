package com.riseup.flimbit.entity;
import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

import java.math.BigDecimal;

@Entity
@Table(name = "payout")
@Getter
@Setter
@NoArgsConstructor       // <-- important for JPA
@AllArgsConstructor
@Builder
public class Payout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int investmentId;
    private Integer userId;
    private Integer movieId;
    private Integer shareTypeId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 20)
    private String status = "Pending";  // Paid / Failed / Pending

    @Column(length = 50)
    private String method;

    private Timestamp paidAt;
    private String note;

    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
    private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
}
