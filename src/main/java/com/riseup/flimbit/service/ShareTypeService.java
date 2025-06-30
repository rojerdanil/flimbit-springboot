package com.riseup.flimbit.service;



import java.util.List;

import com.riseup.flimbit.dto.ShareTypeDTO;
import com.riseup.flimbit.entity.ShareType;
import com.riseup.flimbit.request.ShareTypeRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.ShareTypeResponse;

public interface ShareTypeService {
    CommonResponse create(ShareTypeRequest shareType,String type);
    void delete(Long id);
    List<ShareType> findByMovieId(Long movieId);
    
    List<ShareTypeResponse> getShareTypeByMovieId(Long movieId);

}
