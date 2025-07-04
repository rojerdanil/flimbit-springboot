package com.riseup.flimbit.serviceImp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.MovieStatus;
import com.riseup.flimbit.repository.MovieStatusRepository;
import com.riseup.flimbit.service.MovieStatusService;

import java.util.List;

@Service
public class MovieStatusServiceImpl implements MovieStatusService {

    @Autowired
    private MovieStatusRepository repository;

   

    @Override
    public MovieStatus update(Integer id, MovieStatus status) {
        MovieStatus existing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Status not found"));
        existing.setName(status.getName());
        existing.setDescription(status.getDescription());
        return repository.save(existing);
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Status not found");
        }
        repository.deleteById(id);
    }

    @Override
    public List<MovieStatus> findAll() {
        return repository.findAll();
    }

	@Override
	public MovieStatus create(MovieStatus status) {
		// TODO Auto-generated method stub
		return null;
	}

}
