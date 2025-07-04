package com.riseup.flimbit.repository;

import java.util.Optional;

import org.springframework.data.domain.Pageable; 
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.riseup.flimbit.entity.PromotionType;

public interface PromotionTypeRepository extends JpaRepository<PromotionType, Long> {
    PromotionType findByTypeCode(String typeCode);
    Optional<PromotionType> findByTypeCodeIgnoreCaseAndStatusIgnoreCase(String typeCode, String status);
    Page<PromotionType> findByTypeCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String typeCode, String description, Pageable pageable
        );
}