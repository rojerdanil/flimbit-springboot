package com.riseup.flimbit.controllers;

import com.riseup.flimbit.entity.MovieActor;
import com.riseup.flimbit.request.MovieActorRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.MovieActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movie-actors")
public class MovieActorController {

	@Autowired
	private MovieActorService service;

	@PostMapping(value = "/addMovieActor")
	public CommonResponse create(@RequestBody MovieActorRequest actor) {
		return service.create(actor);
	}

	@PutMapping("/{id}")
	public MovieActor update(@PathVariable Integer id, @RequestBody MovieActor actor) {
		return service.update(id, actor);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Integer id) {
		service.delete(id);
	}

	@GetMapping
	public List<MovieActor> getAll() {
		return service.findAll();
	}
}
