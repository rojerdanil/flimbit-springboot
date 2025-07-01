package com.riseup.flimbit.serviceImp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.UserPromoCode;
import com.riseup.flimbit.repository.UserPromoCodeRepository;
import com.riseup.flimbit.service.UserPromoCodeService;

import java.util.List;

@Service
public class UserPromoCodeServiceImpl implements UserPromoCodeService {

    @Autowired
    private UserPromoCodeRepository repository;

    @Override
    public UserPromoCode save(UserPromoCode userPromoCode) {
        return repository.save(userPromoCode);
    }

    @Override
    public List<UserPromoCode> getAll() {
        return repository.findAll();
    }

    @Override
    public UserPromoCode getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
