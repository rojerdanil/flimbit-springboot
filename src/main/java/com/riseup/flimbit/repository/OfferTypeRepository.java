package com.riseup.flimbit.repository;


import com.riseup.flimbit.entity.OfferType;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferTypeRepository extends JpaRepository<OfferType, Integer> {
    boolean existsByName(String name);
    Page<OfferType> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<OfferType>  findByNameIgnoreCase(String name);
    

}
