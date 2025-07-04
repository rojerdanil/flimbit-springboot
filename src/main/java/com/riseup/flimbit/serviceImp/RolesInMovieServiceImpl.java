package com.riseup.flimbit.serviceImp;

import com.riseup.flimbit.entity.RolesInMovie;
import com.riseup.flimbit.repository.RolesInMovieRepository;
import com.riseup.flimbit.service.RolesInMovieService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolesInMovieServiceImpl implements RolesInMovieService {

    @Autowired
    private RolesInMovieRepository repository;

    @Override
    public RolesInMovie create(RolesInMovie role) {
        return repository.save(role);
    }

    @Override
    public RolesInMovie update(Integer id, RolesInMovie role) {
        RolesInMovie existing = repository.findById(id)
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
    public List<RolesInMovie> findAll() {
        return repository.findAll();
    }
}
