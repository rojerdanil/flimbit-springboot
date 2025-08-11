package com.riseup.flimbit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "invest_offer_money")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestOfferMoney {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    private Integer investId;
    
    
    private int offerId;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "wallet_amount", precision = 10, scale = 2)
    private BigDecimal walletAmount = BigDecimal.ZERO;

    @Column(name = "is_no_platform_commission")
    private boolean isNoPlatformCommission = false;

    @Column(name = "is_no_profit_commission")
    private boolean isNoProfitCommission = false;

    @Column(name = "free_share")
    private int freeShare = 0;

    @Column(name = "offer_name", length = 255)
    private String offerName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "updated_at")
    private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
    
    int totalShare;
    
    int userId;
    
    int movieId;
    
    int shareTypeId;
    
    int  transactionId;
    
    String status;
    
    String orderId;

}
