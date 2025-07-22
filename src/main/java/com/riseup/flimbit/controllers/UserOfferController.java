package com.riseup.flimbit.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.UserOffer;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.UserOfferService;
import com.riseup.flimbit.utility.HttpResponseUtility;

import java.util.List;

@RestController
@RequestMapping("/user-offers")
public class UserOfferController {

    @Autowired
    private UserOfferService service;

    @PostMapping
    public UserOffer create(@RequestBody UserOffer userOffer) {
        return service.save(userOffer);
    }

    @GetMapping
    public List<UserOffer> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public UserOffer getById(@PathVariable int id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }
    
    @GetMapping("/offersbyUser/{id}")
    public ResponseEntity<?> getOffersByUserId(
    		@PathVariable int id) {
     	
    	
    	return null;

        //return HttpResponseUtility.getHttpSuccess(offerService.findById(id)) ;
    }

}
