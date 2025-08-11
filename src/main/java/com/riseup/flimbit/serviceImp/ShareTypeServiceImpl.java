package com.riseup.flimbit.serviceImp;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.MovieShareType;
import com.riseup.flimbit.entity.dto.ShareTypeDTO;
import com.riseup.flimbit.repository.MovieShareTypeRepository;
import com.riseup.flimbit.request.ShareTypeRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.ShareTypeResponse;
import com.riseup.flimbit.service.ShareTypeService;
import com.riseup.flimbit.utility.CommonUtilty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

public class ShareTypeServiceImpl implements ShareTypeService {

	@Autowired
    private  MovieShareTypeRepository repository;

    @Override
    public CommonResponse create(ShareTypeRequest shareTypeRequest,String type) {
    	
    	if(type.trim().equalsIgnoreCase("insert"))
    	{
    		MovieShareType shareType = new MovieShareType();
    		shareType = CommonUtilty.mapRequestToEntity(shareTypeRequest,shareType);
    		return CommonResponse.builder().status(Messages.STATUS_SUCCESS)
    				.message("inserted successfully")
    				.result(repository.save(shareType)).build();
    		
    	}
    	else if(type.trim().equalsIgnoreCase("update"))
    	{
    		
    		Optional<MovieShareType> shareTypeOpt = repository.findById(shareTypeRequest.getId());
    		
    		if(shareTypeOpt.isPresent())
    		{
    			MovieShareType shareType = shareTypeOpt.get();
        		shareType = CommonUtilty.mapRequestToEntity(shareTypeRequest,shareType);
                 

        		return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message("updated succesfully").result(repository.save(shareType)).build();
        		
    		}
    		else
        		return CommonResponse.builder().status(Messages.STATUS_FAILURE).message("record not found").build();

    		
    		
    	}
    	     return CommonResponse.builder().status(Messages.STATUS_FAILURE).message("some error happed").build();
    	
    	
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Override
    public List<MovieShareType> findByMovieId(Long movieId) {
        return repository.findByMovieId(movieId);
    }

	@Override
	public List<ShareTypeResponse> getShareTypeByMovieId(int movieId) {
		// TODO Auto-generated method stub
		List<ShareTypeResponse> shareResList = new ArrayList<ShareTypeResponse>();
		
		 List<ShareTypeDTO> shartTypeDtoList = repository.getShareTypeByMovieId(movieId);
		 
		 if(shartTypeDtoList !=null && shartTypeDtoList.size() > 0 )
		 {
			 for(ShareTypeDTO sharDTO : shartTypeDtoList )
			 {
				 shareResList.add(CommonUtilty.convertDTOToResponse(sharDTO));
			 }
		 }
			 
		return shareResList;
	}

	
}
