package com.riseup.flimbit.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.UserOffer;
import com.riseup.flimbit.entity.dto.UserOfferDto;
import com.riseup.flimbit.repository.UserOfferRepository;
import com.riseup.flimbit.service.UserOfferService;

import java.util.List;

@Service
public class UserOfferServiceImpl implements UserOfferService {

    @Autowired
    private UserOfferRepository repository;

    @Override
    public UserOffer save(UserOffer userOffer) {
        return repository.save(userOffer);
    }

    @Override
    public List<UserOffer> getAll() {
        return repository.findAll();
    }

    @Override
    public UserOffer getById(int id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }

	@Override
	public List<UserOfferDto> getUserOfferByUserId(int id) {
		// TODO Auto-generated method stub
		return null;
	}
}
