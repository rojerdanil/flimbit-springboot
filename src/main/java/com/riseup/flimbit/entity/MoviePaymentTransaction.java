package com.riseup.flimbit.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.sql.Timestamp;


@Entity
@Table(name = "movie_payment_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoviePaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int userId;

    private int movieId;

    private int shareTypeId;

    private Integer investmentId;  // Nullable until payment succeeds

    private BigDecimal amount;

    private String status; // PENDING, SUCCESS, FAILED

    @Column(name = "payment_gateway")
    private String paymentGateway;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "payment_id")
    private String paymentId;

    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
    private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
    
    int totalShare;
    
    BigDecimal perShareAmount  = BigDecimal.valueOf(0);
    
    String internalOrderRef;
}
