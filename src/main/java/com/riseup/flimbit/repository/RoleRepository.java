package com.riseup.flimbit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.riseup.flimbit.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}

