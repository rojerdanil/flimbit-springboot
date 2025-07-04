package com.riseup.flimbit.serviceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.PromoCode;
import com.riseup.flimbit.repository.PromoCodeRepository;
import com.riseup.flimbit.service.PromoCodeService;

import java.util.List;
import java.util.Optional;

@Service
public class PromoCodeServiceImpl implements PromoCodeService {

    @Autowired
    private PromoCodeRepository repository;

    @Override
    public PromoCode save(PromoCode promoCode) {
        return repository.save(promoCode);
    }

    @Override
    public List<PromoCode> getAll() {
        return repository.findAll();
    }

    @Override
    public PromoCode getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Optional<PromoCode> getByCode(String code) {
        return repository.findByPromoCode(code);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
