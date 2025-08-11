package com.riseup.flimbit.service;


import java.util.List;

import org.springframework.data.domain.Page;

import com.riseup.flimbit.entity.Offer;
import com.riseup.flimbit.entity.OfferType;
import com.riseup.flimbit.entity.dto.OfferDTO;
import com.riseup.flimbit.request.OfferRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.dto.OfferMovieDto;

public interface OfferService {
    Offer create(OfferRequest offer);
    List<Offer> findAll();
    Offer findById(Long id);
    CommonResponse delete(Long id);
    Offer update(Long id,OfferRequest offerReq);

    Page<OfferDTO> getPagedOfferTypes(int start, int length, String searchText, String sortColumn, String sortOrder);
    OfferMovieDto getMovieOffer(long movieId);
}
