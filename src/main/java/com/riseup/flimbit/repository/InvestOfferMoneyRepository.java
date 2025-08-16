package com.riseup.flimbit.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.InvestOfferMoney;

@Repository
public interface InvestOfferMoneyRepository extends JpaRepository<InvestOfferMoney, Integer> {

    List<InvestOfferMoney> findByInvestId(Integer investId);

    List<InvestOfferMoney> findByOfferId(Integer offerId);
    
    List<InvestOfferMoney> findByMovieIdAndShareTypeIdAndUserId(int movieId,int shareTypeId,int userId);
    

    boolean existsByInvestIdAndOfferId(Integer investId, Integer offerId);
    
    @Query("SELECT COALESCE(SUM(i.walletAmount), 0) FROM InvestOfferMoney i WHERE i.orderId = :orderId")
    BigDecimal getTotalWalletAmountByOrderId(@Param("orderId") String orderId);
    
    @Modifying
    @Query(
        value = "UPDATE invest_offer_money " +
                "SET status = :status, updated_at = CURRENT_TIMESTAMP ,invest_id = :investId " +
                "WHERE order_id = :orderId",
        nativeQuery = true
    )
    int updateStatusByOrderId(@Param("status") String status,
    		                   @Param("investId") int investId,
                              @Param("orderId") String orderId);
}