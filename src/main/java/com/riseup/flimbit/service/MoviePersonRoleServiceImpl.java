package com.riseup.flimbit.service;

import com.riseup.flimbit.entity.MoviePersonType;
import com.riseup.flimbit.repository.MoviePersonRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoviePersonRoleServiceImpl implements MoviePersonRoleService {

    @Autowired
    private MoviePersonRoleRepository repository;

    @Override
    public MoviePersonType create(MoviePersonType role) {
        return repository.save(role);
    }

    @Override
    public MoviePersonType update(Integer id, MoviePersonType role) {
        MoviePersonType existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        existing.setName(role.getName());
        existing.setDescription(role.getDescription());
        return repository.save(existing);
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<MoviePersonType> findAll() {
        return repository.findAll();
    }
}
