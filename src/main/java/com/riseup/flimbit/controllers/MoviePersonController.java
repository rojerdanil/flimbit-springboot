package com.riseup.flimbit.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.entity.MoviePerson;
import com.riseup.flimbit.request.MoviePersonRequest;
import com.riseup.flimbit.service.MoviePersonService;
import com.riseup.flimbit.utility.CommonUtilty;

import java.util.List;

@RestController
@RequestMapping("/movie-person")
public class MoviePersonController {

    @Autowired
    private MoviePersonService service;

    @PostMapping(value = "/addMoviePerson")
    public ResponseEntity<?> create(@RequestBody MoviePersonRequest person) {
    	
    	
        return ResponseEntity.status(HttpStatus.OK).body(service.create(person));

        
    }

    @PutMapping("/{id}")
    public MoviePerson update(@PathVariable Integer id, @RequestBody MoviePerson person) {
        return service.update(id, person);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping
    public List<MoviePerson> findAll() {
        return service.findAll();
    }
}
