package com.riseup.flimbit.service;

import com.riseup.flimbit.entity.OfferType;
import com.riseup.flimbit.request.OfferTypeRequest;
import com.riseup.flimbit.response.CommonResponse;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

public interface OfferTypeService {
    OfferType createOfferType(OfferTypeRequest offerType);
    List<OfferType> getAllOfferTypes();
    Optional<OfferType> getOfferTypeById(Integer id);
    OfferType updateOfferType(Integer id, OfferTypeRequest updated);
    CommonResponse deleteOfferType(Integer id);
    Page<OfferType> getPagedOfferTypes(int start, int length, String searchText, String sortColumn, String sortOrder);

    
}
