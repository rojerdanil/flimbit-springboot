package com.riseup.flimbit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.ApplicationProperty;

@Repository
public interface ApplicationPropertyRepository extends JpaRepository<ApplicationProperty, Integer> {

    Optional<ApplicationProperty> findByPropertyName(String propertyName);
}
