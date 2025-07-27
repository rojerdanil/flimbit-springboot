package com.riseup.flimbit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.entity.AdminUser;
import com.riseup.flimbit.entity.dto.AdminUserDTO;
import com.riseup.flimbit.entity.dto.UserWithStatusDTO;
import com.riseup.flimbit.request.AdminUserRequest;
import com.riseup.flimbit.service.AdminUserService;
import com.riseup.flimbit.utility.HttpResponseUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin-users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

  @GetMapping
    public List<AdminUser> getAllAdminUsers() {
        return adminUserService.getAllAdminUsers();
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<?>  getAdminUserById(@PathVariable int id) {
        AdminUser adminUser = adminUserService.getAdminUserById(id)
        		              .orElseThrow(() -> new RuntimeException("Admin user is not found " + id));
        return  HttpResponseUtility.getHttpSuccess(adminUser);
    }

    @PostMapping("/create")
    public ResponseEntity<?> addAdminUser(@RequestBody AdminUserRequest adminUser) {
        return HttpResponseUtility.getHttpSuccess(adminUserService.addAdminUser(adminUser));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateAdminUser(@PathVariable int id, @RequestBody AdminUserRequest adminUser) {
      
            AdminUser updatedUser = adminUserService.updateAdminUser(id, adminUser);
            return HttpResponseUtility.getHttpSuccess(updatedUser);
      
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deleteAdminUser(@PathVariable int id) {
        
            adminUserService.deleteAdminUser(id);
            return HttpResponseUtility.getHttpSuccess("deleted successfully");
      
    }
    
    @GetMapping("/dataTableAdminUsers")
    public ResponseEntity<?> getAdminUsers(
            @RequestParam int draw,
            @RequestParam int start,
            @RequestParam int length,
            @RequestParam(required = false) String searchText,
            @RequestParam(defaultValue = "id") String sortColumn,	
            @RequestParam(defaultValue = "asc") String sortOrder,	
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)  int  roleType

    ) {
		
		
		
    	 status = status == null || status.isEmpty() ? null : status;
    	 searchText = searchText == null || searchText.isEmpty() ? null : searchText;
    	System.out.println("comming " +  roleType+ " "+ status + " "+ searchText);

	    Page<AdminUserDTO> page = adminUserService.getAdminUsersWithStatus
	    		( roleType,status,searchText,start, length,  sortColumn, sortOrder);
	    Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", page.getTotalElements());
        response.put("recordsFiltered", page.getTotalElements());
        response.put("data", page.getContent());
        return HttpResponseUtility.getHttpSuccess(response);
	}

}
