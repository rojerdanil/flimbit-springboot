	package com.riseup.flimbit.serviceImp;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.UserPayoutInitiation;
import com.riseup.flimbit.entity.dto.UserPayoutInitiationDTO;
import com.riseup.flimbit.repository.UserPayoutInitiationRepository;
import com.riseup.flimbit.service.UserPayoutInitiationService;

@Service
	public class UserPayoutInitiationServiceImpl implements UserPayoutInitiationService {

	    @Autowired
	    private UserPayoutInitiationRepository repository;

	    @Override
	    public List<UserPayoutInitiation> findAllFiltered(Integer movieId, Integer shareTypeId) {
	        List<UserPayoutInitiation> all = repository.findAll();
	        return all.stream()
	                .filter(p -> (movieId == null || p.getMovieId() == movieId))
	                .filter(p -> (shareTypeId == null || p.getShareTypeId() == shareTypeId))
	                .collect(Collectors.toList());
	    }

		@Override
		public Page<UserPayoutInitiationDTO> gePayoutInitiationForDataTable(int language, int movie, String status,
				String searchText, int start, int length, String sortColumn, String sortOrder) {
			// TODO Auto-generated method stub
			   int page = start / length;
			    
			    System.out.println(language + movie + status);

		        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
		        Pageable pageable = PageRequest.of(page, length, sort);
		        return repository.gePayoutInitiationForDataTable(language, movie, status, searchText, pageable);
		}

		@Override
		public List<UserPayoutInitiationDTO> getPayoutInitiationForUserIdAndMovieId(int userId, int movId) {
			// TODO Auto-generated method stub
			return repository.getPayoutInitiationForUserIdAndMovieId(userId, movId);
		}
	}