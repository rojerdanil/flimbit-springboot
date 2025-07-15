package com.riseup.flimbit.service;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.riseup.flimbit.entity.PromoCode;
import com.riseup.flimbit.entity.PromotionReward;
import com.riseup.flimbit.request.PromoCodeRequest;
import com.riseup.flimbit.request.PromoRewardMapRequest;

public interface PromoCodeService {
    PromoCode savePromoCodeRewards(PromoRewardMapRequest promoCode);
    List<PromoCode> getAll();
    PromoCode getById(Long id);
    Optional<PromoCode> getByCode(String code);
    void delete(Long id);
    
    PromoCode savePromoCode(PromoCodeRequest promoRequst);
    PromoCode updatePromoCode(PromoCodeRequest promoRequst);

    Page<PromoCode> getPaginated(int start, int length, String searchText, String sortColumn, String sortOrder);

}
