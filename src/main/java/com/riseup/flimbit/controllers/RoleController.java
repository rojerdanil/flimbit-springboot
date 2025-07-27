package com.riseup.flimbit.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riseup.flimbit.entity.Role;
import com.riseup.flimbit.serviceImp.RoleServiceImp;
import com.riseup.flimbit.utility.HttpResponseUtility;

@RestController
@RequestMapping("/roles")
public class RoleController {
	
	@Autowired
	RoleServiceImp roleService;
	
	 @GetMapping("/allRoles")
	    public ResponseEntity<?>  getAllRoles() {
	        return HttpResponseUtility.getHttpSuccess( roleService.findAllRoles());
	    }

}
	