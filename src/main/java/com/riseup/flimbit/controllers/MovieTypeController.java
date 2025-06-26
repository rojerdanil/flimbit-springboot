package com.riseup.flimbit.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.entity.MovieType;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.MovieTypeService;
import com.riseup.flimbit.utility.JwtService;

import java.util.List;

@RestController
@RequestMapping("/movie-types")
public class MovieTypeController {
	
	@Autowired
	JwtService jwtService;
	@Value("${isValidateTokenEnable}")
    boolean isValidateTokenEnable;

    @Autowired
    private MovieTypeService movieTypeService;

    @PostMapping
    public MovieType add(@RequestBody MovieType movieType) {
        return movieTypeService.addMovieType(movieType);
    }

    @PutMapping("/{id}")
    public MovieType update(@PathVariable Integer id, @RequestBody MovieType movieType) {
        movieType.setId(id);
        return movieTypeService.updateMovieType(movieType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        movieTypeService.deleteMovieType(id);
    }

    @GetMapping("/{id}")
    public MovieType getById(@PathVariable Integer id) {
        return movieTypeService.getMovieTypeById(id);
    }

    @GetMapping("/alltypes")
    public ResponseEntity<?>getAll(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken) {
    	
    	if(isValidateTokenEnable)
		{	
    		CommonResponse	 commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.builder().status(Messages.STATUS_SUCCESS).result(movieTypeService.getAllMovieTypes()).build());

         
    }
}
