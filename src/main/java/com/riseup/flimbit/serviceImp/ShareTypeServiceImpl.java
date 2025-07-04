package com.riseup.flimbit.serviceImp;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.dto.ShareTypeDTO;
import com.riseup.flimbit.entity.ShareType;
import com.riseup.flimbit.repository.ShareTypeRepository;
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
    private  ShareTypeRepository repository;

    @Override
    public CommonResponse create(ShareTypeRequest shareTypeRequest,String type) {
    	
    	if(type.trim().equalsIgnoreCase("insert"))
    	{
    		ShareType shareType = new ShareType();
    		shareType = CommonUtilty.mapRequestToEntity(shareTypeRequest,shareType);
    		return CommonResponse.builder().status(Messages.STATUS_SUCCESS)
    				.message("inserted successfully")
    				.result(repository.save(shareType)).build();
    		
    	}
    	else if(type.trim().equalsIgnoreCase("update"))
    	{
    		
    		Optional<ShareType> shareTypeOpt = repository.findById(shareTypeRequest.getId());
    		
    		if(shareTypeOpt.isPresent())
    		{
    			ShareType shareType = shareTypeOpt.get();
        		shareType = CommonUtilty.mapRequestToEntity(shareTypeRequest,shareType);
                 

        		return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message("updated succesfully").result(repository.save(shareType)).build();
        		
    		}
    		else
        		return CommonResponse.builder().status(Messages.STATUS_FAILURE).message("record not found").build();

    		
    		
    	}
    	     return CommonResponse.builder().status(Messages.STATUS_FAILURE).message("some error happed").build();
    	
    	
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<ShareType> findByMovieId(Long movieId) {
        return repository.findByMovieId(movieId);
    }

	@Override
	public List<ShareTypeResponse> getShareTypeByMovieId(Long movieId) {
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
