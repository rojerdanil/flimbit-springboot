package com.riseup.flimbit.entity;

import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "promotion_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionType {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_promotion_type_id")
	@SequenceGenerator(name = "seq_promotion_type_id", sequenceName = "seq_promotion_type_id", allocationSize = 1)
    private Long id;

    @Column(name = "type_code", nullable = false, unique = true)
    private String typeCode;

    private String description;

    @Column(name = "status", nullable = false)
    private String status = "Active";  // Default is active

    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
    
    int userCount;
    int expiryDays;
    
    int prizeAmount;
}
