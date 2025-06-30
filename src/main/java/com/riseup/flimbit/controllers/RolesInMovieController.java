package com.riseup.flimbit.controllers;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.RolesInMovie;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.RolesInMovieService;
import com.riseup.flimbit.utility.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles-in-movie")
public class RolesInMovieController {

    @Autowired
    private RolesInMovieService service;
    
    @Autowired
	JwtService jwtService;
	@Value("${isValidateTokenEnable}")
    boolean isValidateTokenEnable;
	

    @PostMapping("/addRoleInMovie")
    public RolesInMovie create(@RequestBody RolesInMovie role) {
    	
        return service.create(role);
    }

    @PutMapping("/{id}")
    public RolesInMovie update(@PathVariable Integer id, @RequestBody RolesInMovie role) {
        return service.update(id, role);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping("/getAllRole")
    public ResponseEntity<?>  findAll(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken) {
    	
   	 	if(isValidateTokenEnable)
 		{	
 		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
            if (commonToken.getStatus() != Messages.SUCCESS) {
 	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
 	        }
 		}
         return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder()
         		.status(Messages.STATUS_SUCCESS)
         		.message("Data reads")
         		.result(service.findAll()).build());
    }

}
