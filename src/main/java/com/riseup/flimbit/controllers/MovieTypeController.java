package com.riseup.flimbit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.entity.MovieType;
import com.riseup.flimbit.service.MovieTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/movie-types")
public class MovieTypeController {

    @Autowired
    private MovieTypeService movieTypeService;

    @PostMapping
    public MovieType add(@RequestBody MovieType movieType) {
        return movieTypeService.addMovieType(movieType);
    }

    @PutMapping("/{id}")
    public MovieType update(@PathVariable Integer id, @RequestBody MovieType movieType) {
        movieType.setId(id);
        return movieTypeService.updateMovieType(movieType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        movieTypeService.deleteMovieType(id);
    }

    @GetMapping("/{id}")
    public MovieType getById(@PathVariable Integer id) {
        return movieTypeService.getMovieTypeById(id);
    }

    @GetMapping
    public List<MovieType> getAll() {
        return movieTypeService.getAllMovieTypes();
    }
}
