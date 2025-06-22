package com.riseup.flimbit.repository;

import com.riseup.flimbit.entity.MoviePersonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoviePersonRoleRepository extends JpaRepository<MoviePersonType, Integer> {
}
