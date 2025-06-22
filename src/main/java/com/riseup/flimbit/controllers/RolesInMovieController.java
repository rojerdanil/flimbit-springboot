package com.riseup.flimbit.controllers;

import com.riseup.flimbit.entity.RolesInMovie;
import com.riseup.flimbit.service.RolesInMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles-in-movie")
public class RolesInMovieController {

    @Autowired
    private RolesInMovieService service;

    @PostMapping
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

    @GetMapping
    public List<RolesInMovie> findAll() {
        return service.findAll();
    }
}
