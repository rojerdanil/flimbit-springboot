package com.riseup.flimbit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.riseup.flimbit.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	Optional<User> findByphoneNumber(String phoneNumber);
	
}
