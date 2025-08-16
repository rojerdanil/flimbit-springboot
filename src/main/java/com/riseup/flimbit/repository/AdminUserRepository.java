package com.riseup.flimbit.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.riseup.flimbit.entity.AdminUser;
import com.riseup.flimbit.entity.dto.AdminUserDTO;
import com.riseup.flimbit.entity.dto.UserWithStatusWebDto;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Integer> {

	@Query(value = """
		    SELECT au.*, rol.name AS roleName
		    FROM admin_users au
		    LEFT JOIN roles rol ON au.role_id = rol.id
		    WHERE 
		      (:search IS NULL OR (
		          LOWER(au.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
		          LOWER(au.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
		          au.phone LIKE CONCAT('%', :search, '%')
		      ))
		      AND (:status IS NULL OR LOWER(au.status) = LOWER(:status))
		      AND (:role = 0 OR au.role_id = :role)
		    """, nativeQuery = true)
	Page<AdminUserDTO> fetchAllUsersWithStatus(@Param("role") int roleId, @Param("status") String status,
			@Param("search") String searchText, Pageable pageable);

	Optional<AdminUser> findByPhoneIgnoreCase(String phone);
	
	
	@Query("SELECT u FROM AdminUser u WHERE " +
		       "(LOWER(u.email) = LOWER(:input) OR LOWER(u.name) = LOWER(:input) OR u.phone = :input) " +
		       "AND u.passwordHash = :password")
		Optional<AdminUser> findByLoginInput(@Param("input") String input, @Param("password") String password);

	Optional<AdminUser>  findByToken(String token);
	Optional<AdminUser>  findByRefreshToken(String token);
	Optional<AdminUser>  findByDeviceId(String deviceId);
	Optional<AdminUser> findByNameIgnoreCase(String name);

	


}
