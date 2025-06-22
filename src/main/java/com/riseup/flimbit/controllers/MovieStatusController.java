package com.riseup.flimbit.controllers;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.entity.MovieStatus;
import com.riseup.flimbit.service.MovieStatusService;

import java.util.List;

@RestController
@RequestMapping("/api/movie-status")
public class MovieStatusController {

    @Autowired
    private MovieStatusService service;

    @PostMapping
    public MovieStatus create(@RequestBody MovieStatus status) {
        return service.create(status);
    }

    @PutMapping("/{id}")
    public MovieStatus update(@PathVariable Integer id, @RequestBody MovieStatus status) {
        return service.update(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping
    public List<MovieStatus> findAll() {
        return service.findAll();
    }
}
