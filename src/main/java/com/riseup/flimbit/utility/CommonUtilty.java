package com.riseup.flimbit.utility;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.riseup.flimbit.entity.Movie;
import com.riseup.flimbit.entity.MovieActor;
import com.riseup.flimbit.entity.MoviePerson;
import com.riseup.flimbit.entity.MovieShareSummaryInterface;
import com.riseup.flimbit.entity.MovieStatus;
import com.riseup.flimbit.request.MovieActorRequest;
import com.riseup.flimbit.request.MoviePersonRequest;
import com.riseup.flimbit.request.MovieRequest;
import com.riseup.flimbit.response.MovieResponse;

public class CommonUtilty {
	
	public static boolean checkEmptyOrNull(String name)
	{
		return Optional.ofNullable(name).filter(s -> !s.isEmpty()).isPresent();
	}
	public static MoviePerson mapMovPersonReqToMoviePerson(MoviePersonRequest movPerReq)
	{

		if(Optional.ofNullable(movPerReq).isPresent())
		{
			MoviePerson moviePerson = new MoviePerson();
			if(movPerReq.getId() != 0)
			moviePerson.setId(moviePerson.getId());
            if(checkEmptyOrNull(movPerReq.getName() ))
            	moviePerson.setName(movPerReq.getName());
            if(checkEmptyOrNull(movPerReq.getGender() ))
            	moviePerson.setGender(movPerReq.getGender());
            if(checkEmptyOrNull(movPerReq.getImageUrl() ))
            	moviePerson.getImageUrl();
			if(movPerReq.getAwardsCount() != 0)
				moviePerson.setAwardsCount(movPerReq.getAwardsCount());
			if(movPerReq.getLanguage() != 0)
			{

				moviePerson.setLanguage(movPerReq.getLanguage());
			}
			if(movPerReq.getRole() != 0)
				moviePerson.setRole(movPerReq.getRole());
			if(movPerReq.getPopularityScore() != 0)
				moviePerson.setPopularityScore(movPerReq.getPopularityScore());

             return moviePerson;	
		}
		else
			return null;
	}
	
	public static MovieActor mapMovieActorReqToMovieActor(MovieActorRequest movieActorReq)
	{
		if(Optional.ofNullable(movieActorReq).isPresent())
		{
			MovieActor movieActor = new MovieActor();
			if(movieActorReq.getActorId() != 0)
				movieActor.setActorId(movieActorReq.getActorId());
			if(movieActorReq.getMovieId()!=0)
		         movieActor.setMovieId(movieActorReq.getMovieId());
			if(movieActorReq.getRoleMovieId()!=0)
				movieActor.setRoleMovieId(movieActorReq.getRoleMovieId());
			return movieActor;
		}
		return null;
	}
	public static List<MovieResponse>  mapToMovieRespFromMoiveEntity(List<MovieShareSummaryInterface> movieList)
	{
		List<MovieResponse> movResList = new ArrayList<MovieResponse>();
		if(Optional.ofNullable(movieList).isPresent())
		{
			String dateFormat = "yyyy MMM dd";

			
			for(MovieShareSummaryInterface movie : movieList)
			{
				MovieResponse mresponse = MovieResponse.builder().id(movie.getId())
		                 .budget(movie.getBudget())
		                 .description(movie.getDescription())
		                 .language(movie.getLanguage())
		                 .perShareAmount(movie.getPerShareAmount())
		                 .status(movie.getStatus())
		                 .title(movie.getTitle())
		                 .investedAmount(movie.getInvestedAmount())
		                 .releaseDate(movie.getReleaseDate() != null ?
		                		 convertTImeStampToString(movie.getReleaseDate(),dateFormat):"Coming Soon")
		                 .trailerDate(movie.getTrailerDate() != null ?
		                		 convertTImeStampToString(movie.getTrailerDate(),dateFormat):"Coming Soon")
		                 .createdDate(convertTImeStampToString(movie.getCreatedDate(),dateFormat))
		                 .updatedDate(convertTImeStampToString(movie.getUpdatedDate(),dateFormat))
		                 .investedPercentage(movie.getInvestedPercentage())
		                 .trailerUrl(movie.getTrailerUrl())
		                 .posterUrl(movie.getPosterUrl())
		                 .movieType(movie.getMovieTypeName())
		                 .actorDetails(movie.getCast())
		                 .build();
			movResList.add(mresponse);
		
			}
		}
		return movResList;
	}
	
	public static Movie  mapToMovieEnity(MovieRequest movieReq,Movie movies)
	{
		if(Optional.ofNullable(movieReq).isPresent())
		{
            if(movieReq.getBudget() != 0)
			 movies.setBudget(movieReq.getBudget());
            if(checkEmptyOrNull(movieReq.getDescription()))
			  movies.setDescription(movieReq.getDescription());
            if(checkEmptyOrNull(movieReq.getLanguage()))
			   movies.setLanguage(movieReq.getLanguage().trim());
            if(checkEmptyOrNull(movieReq.getTitle()))
     			movies.setTitle(movieReq.getTitle());
            if(checkEmptyOrNull(movieReq.getReleaseDate()))
     			movies.setReleaseDate(getTimeStampFromText(movieReq.getReleaseDate()));
            if(movieReq.getStatus() > 0)
     			movies.setStatusId(movieReq.getStatus());
            if(movieReq.getPerShareAmount() > 0)
     			movies.setPerShareAmount(movieReq.getPerShareAmount());
            if(checkEmptyOrNull(movieReq.getTrailerDate()))
     			movies.setTrailerDate(getTimeStampFromText(movieReq.getTrailerDate()));
            if(movieReq.getMovieTypeId() !=0)
     			movies.setMovieTypeId(movieReq.getMovieTypeId());
            if(checkEmptyOrNull(movieReq.getPosterUrl()))
     			movies.setPosterUrl(movieReq.getPosterUrl());
            if(checkEmptyOrNull(movieReq.getTrailerUrl()))
     			movies.setTrailerUrl(movieReq.getTrailerUrl());
			return movies;
		}
		return null;
	}
	
	public static String convertTImeStampToString(Timestamp timeValue , String dateformat)
	{
		//"yyyy MMM dd"
	     DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateformat);
	     LocalDateTime localDateTime = timeValue.toLocalDateTime();
	     String formattedData = localDateTime.format(formatter);
	     return formattedData;

	}
 public static Timestamp  getTimeStampFromText(String timestampString)
 {
	 //String timestampString = "2023-10-26 14:30:00.123";
     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

     try {
         // Parse the string into a LocalDateTime
         LocalDateTime localDateTime = LocalDateTime.parse(timestampString, formatter);

         // Convert LocalDateTime to java.sql.Timestamp
         Timestamp sqlTimestamp = Timestamp.valueOf(localDateTime);

         System.out.println("Original String: " + timestampString);
         System.out.println("Converted SQL Timestamp: " + sqlTimestamp);
         return sqlTimestamp;
     } catch (Exception e) {
         System.err.println("Error converting timestamp: " + e.getMessage());
     }
     return null;
 }
 public static Map<Integer,String> getMovieStatusKeyValuePair(List<MovieStatus> movieStatusList)
 {
	 return movieStatusList.stream().collect(Collectors.toMap(MovieStatus::getId, MovieStatus::getName));

 }
}
