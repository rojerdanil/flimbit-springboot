package com.riseup.flimbit.service;



import java.util.List;

import com.riseup.flimbit.entity.MovieShareType;
import com.riseup.flimbit.entity.dto.ShareTypeDTO;
import com.riseup.flimbit.request.ShareTypeRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.ShareTypeResponse;

public interface ShareTypeService {
    CommonResponse create(ShareTypeRequest shareType,String type);
    void delete(int id);
    List<MovieShareType> findByMovieId(Long movieId);
    
    List<ShareTypeResponse> getShareTypeByMovieId(int movieId);

}
