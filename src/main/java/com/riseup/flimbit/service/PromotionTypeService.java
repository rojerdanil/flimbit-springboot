package com.riseup.flimbit.service;

import com.riseup.flimbit.entity.PromotionType;
import com.riseup.flimbit.request.PromotionTypeRequest;
import com.riseup.flimbit.response.CommonResponse;

import java.util.List;

import org.springframework.data.domain.Page;

public interface PromotionTypeService {
    Page<PromotionType> getPaginated(int start, int length, String searchText, String sortColumn, String sortOrder);
    CommonResponse save(PromotionTypeRequest promotionType);
    CommonResponse deleteById(Long id);
    List<PromotionType> findAllRecords();
}
