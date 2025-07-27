package com.riseup.flimbit.serviceImp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.Role;
import com.riseup.flimbit.repository.RoleRepository;

@Service
public class RoleServiceImp {
	
	@Autowired
	RoleRepository roleRepository;
	
	 public List<Role> findAllRoles() {
	        return roleRepository.findAll();
	    }

}
