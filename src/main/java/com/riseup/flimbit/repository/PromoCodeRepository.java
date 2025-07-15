package com.riseup.flimbit.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.PromoCode;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    Optional<PromoCode> findByPromoCode(String promoCode);
    
    @Query("SELECT p FROM PromoCode p WHERE LOWER(p.promoCode) LIKE LOWER(CONCAT('%', :search, '%')) ")
    Page<PromoCode> searchByCodeOrType(@Param("search") String search, Pageable pageable);
    
    
    
}
