package com.riseup.flimbit.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.riseup.flimbit.entity.UserPayoutInitiation;
import com.riseup.flimbit.entity.dto.PayoutDTO;
import com.riseup.flimbit.entity.dto.UserPayoutInitiationDTO;

public interface UserPayoutInitiationService {
    List<UserPayoutInitiation> findAllFiltered(Integer movieId, Integer shareTypeId);
    
    
    public Page<UserPayoutInitiationDTO> gePayoutInitiationForDataTable(int language, int movie, String status, String searchText,int start, int length, String sortColumn, String sortOrde);
  
    
    List<UserPayoutInitiationDTO> getPayoutInitiationForUserIdAndMovieId(int userId, int movId);



}
