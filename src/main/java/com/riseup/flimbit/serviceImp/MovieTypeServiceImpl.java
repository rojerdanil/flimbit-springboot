package com.riseup.flimbit.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.MovieType;
import com.riseup.flimbit.repository.MovieTypeRepository;
import com.riseup.flimbit.service.MovieTypeService;

import java.util.List;
import java.util.Optional;

@Service
public class MovieTypeServiceImpl implements MovieTypeService {

	@Autowired
	private MovieTypeRepository repository;

	@Override
	public MovieType addMovieType(MovieType movieType) {
		return repository.save(movieType);
	}

	@Override
	public MovieType updateMovieType(MovieType movieType) {
		if (movieType.getId() == null || !repository.existsById(movieType.getId())) {
			throw new RuntimeException("MovieType not found or ID is missing");
		}
		return repository.save(movieType);
	}

	@Override
	public void deleteMovieType(Integer id) {
		if (!repository.existsById(id)) {
			throw new RuntimeException("MovieType not found");
		}
		repository.deleteById(id);
	}

	@Override
	public MovieType getMovieTypeById(Integer id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("MovieType not found"));
	}

	@Override
	public List<MovieType> getAllMovieTypes() {
		// TODO Auto-generated method stub
		return repository.findAll();
	}

}
