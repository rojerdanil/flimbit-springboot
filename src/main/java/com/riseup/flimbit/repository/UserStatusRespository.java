package com.riseup.flimbit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.riseup.flimbit.entity.UserStatus;
import com.riseup.flimbit.entity.User;

public interface UserStatusRespository extends JpaRepository<UserStatus, Long> {
	
	Optional<UserStatus> findByuserId(int userId );

}
