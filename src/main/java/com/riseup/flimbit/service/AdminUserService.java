package com.riseup.flimbit.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.riseup.flimbit.entity.AdminUser;
import com.riseup.flimbit.entity.dto.AdminUserDTO;
import com.riseup.flimbit.request.AdminUserRequest;
import com.riseup.flimbit.request.LoginRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.TokenResponse;

public interface AdminUserService {
	
	 List<AdminUser> getAllAdminUsers();
	    
	    AdminUser addAdminUser(AdminUserRequest adminUser);
	    
	    AdminUser updateAdminUser(int id, AdminUserRequest adminUser);
	    
	    void deleteAdminUser(int id);
	    
	    Optional<AdminUser> getAdminUserById(int id);
	    
	    Page<AdminUserDTO> getAdminUsersWithStatus(int roleId, 
	    		 String status, String searchText, int start, int length, String sortColumn, String sortOrder);

	    public TokenResponse loginWithIdentifier(LoginRequest loginRequest) ;
	    
		public CommonResponse validateWebToken(String authHeader,String deviceId) ;
		
		public AdminUser  validateWebTokenFilter(String authHeader,String deviceId);
		
		public CommonResponse validateRefreshWebToken(String authHeader,String deviceId) ;


}
