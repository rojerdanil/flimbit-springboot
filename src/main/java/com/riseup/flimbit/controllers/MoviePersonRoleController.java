package com.riseup.flimbit.controllers;

import com.riseup.flimbit.entity.MoviePersonType;
import com.riseup.flimbit.service.MoviePersonRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movie-person-role")
public class MoviePersonRoleController {

    @Autowired
    private MoviePersonRoleService service;

    @PostMapping
    public MoviePersonType create(@RequestBody MoviePersonType role) {
        return service.create(role);
    }

    @PutMapping("/{id}")
    public MoviePersonType update(@PathVariable Integer id, @RequestBody MoviePersonType role) {
        return service.update(id, role);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping
    public List<MoviePersonType> findAll() {
        return service.findAll();
    }
}
