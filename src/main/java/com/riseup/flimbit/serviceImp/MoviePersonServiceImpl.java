package com.riseup.flimbit.serviceImp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.MoviePerson;
import com.riseup.flimbit.repository.MoviePersonRepository;
import com.riseup.flimbit.request.MoviePersonRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.MoviePersonService;
import com.riseup.flimbit.utility.CommonUtilty;

import java.util.List;
import java.util.Optional;

@Service
public class MoviePersonServiceImpl implements MoviePersonService {

    @Autowired
    private MoviePersonRepository movPersRepository;

    @Override
    public CommonResponse create(MoviePersonRequest personRequest) {
    	
      // name and language is mandatory
    	
    	Optional<MoviePerson>  moviePerOpt =movPersRepository.findByNameIgnoreCaseAndLanguage(personRequest.getName(),personRequest.getLanguage());
    	if(moviePerOpt.isPresent())
    	{
            return CommonResponse.builder().status(Messages.STATUS_FAILURE).message("Movie Person Name "+personRequest.getName() + " is available with same language " +personRequest.getLanguage()).build();

    	}
    	
       MoviePerson per =  movPersRepository.save(CommonUtilty.mapMovPersonReqToMoviePerson(personRequest));
        return CommonResponse.builder().status(Messages.STATUS_SUCCESS).message(Messages.STATUS_UPATE_SUCCESS).result(per).build();

    }

    @Override
    public MoviePerson update(Integer id, MoviePerson person) {
        person.setId(id);
        return movPersRepository.save(person);
    }

    @Override
    public void delete(Integer id) {
    	movPersRepository.deleteById(id);
    }

    @Override
    public List<MoviePerson> findAll() {
        return movPersRepository.findAll();
    }

	@Override
	public List<MoviePerson> findAllByLanguagId(int id) {
		// TODO Auto-generated method stub
		return movPersRepository.findByLanguage(id);
	}
}
