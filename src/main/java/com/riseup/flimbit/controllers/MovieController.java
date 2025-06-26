package com.riseup.flimbit.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.request.DataTableRequest;
import com.riseup.flimbit.request.DeleteRequest;
import com.riseup.flimbit.request.MovieRequest;
import com.riseup.flimbit.request.MovieSearchRequest;
import com.riseup.flimbit.request.UserRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.service.MovieService;
import com.riseup.flimbit.utility.JwtService;

import jakarta.persistence.NamedStoredProcedureQueries;

@RestController
@RequestMapping(value = "/movie")
public class MovieController {
	Logger logger
    = LoggerFactory.getLogger(MovieController.class);
	
	@Autowired
	JwtService jwtService;
	@Value("${isValidateTokenEnable}")
    boolean isValidateTokenEnable;
	
	@Autowired
	MovieService movieService;
	
	
	@Value("${movie.upload-dir}")
	private String uploadDir;
	
	@PostMapping("/listMovies")
    public ResponseEntity<?> listMovies(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody MovieSearchRequest movieSearchRequest
    		)
    {
		if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
		
        return ResponseEntity.status(HttpStatus.OK).body(movieService.getMoviesByLanguage(movieSearchRequest));
    }
	
	
	@PostMapping("/updateMovie")
    public ResponseEntity<?> updateMovie(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody MovieRequest movieRequest)
    {
		if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(movieService.updateMovie(movieRequest));
    }
	
	@PostMapping(value ="/addMovie",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addMovie(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestPart("movie") MovieRequest movieRequest,
    		@RequestPart(value = "poster", required = false) MultipartFile posterFile)
    {
		if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
		
		String posterPath = "";
	    if (posterFile != null && !posterFile.isEmpty()) {
	    	String safeTitle = movieRequest.getTitle().replaceAll("[^a-zA-Z0-9-_]", "_");
	        String extension = FilenameUtils.getExtension(posterFile.getOriginalFilename()); // e.g., jpg, png
	        String fileName = safeTitle + "." + extension;
	        
	        try {
	            Path targetPath = Paths.get(uploadDir + fileName);
	            Files.copy(posterFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
	            posterPath = targetPath.toString(); // Save this in DB
	        } catch (IOException e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
	        }
	    }
	    movieRequest.setPosterUrl(posterPath);
		
        return ResponseEntity.status(HttpStatus.OK).body(movieService.addMovie(movieRequest));

    }
	@PostMapping("/deletMovie")
    public ResponseEntity<?> deletMovie(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody DeleteRequest movieIds)
    {
		if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
        return ResponseEntity.status(HttpStatus.OK).body(movieService.deleteMovie(movieIds.getIdsList()));
    }

    @PostMapping("/datatable")
    public ResponseEntity<?> getMoviesForDataTable(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		@RequestBody DataTableRequest request) {
    	if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
        return ResponseEntity.status(HttpStatus.OK).body(movieService.getMoviesForDataTable(request));
    }
    
    @GetMapping("/moviebyId")
    public ResponseEntity<?> getMoviesForDataTable(@RequestHeader(value="deviceId") String deviceId,
    		@RequestHeader(value="phoneNumber") String phoneNumber,
    		@RequestHeader(value="accessToken") String accessToken,
    		 @RequestParam("id") int id
    		) {
    	if(isValidateTokenEnable)
		{	
		 CommonResponse commonToken = jwtService.validateToken(accessToken, deviceId, phoneNumber);
           if (commonToken.getStatus() != Messages.SUCCESS) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(commonToken);
	        }
		}
        return ResponseEntity.status(HttpStatus.OK).body(movieService.findMovieSummaryById(id));
    }
    		
    		
    		
    		
}
