package com.riseup.flimbit.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.entity.ApplicationProperty;
import com.riseup.flimbit.repository.ApplicationPropertyRepository;

import jakarta.annotation.PostConstruct;

@Service
public class TokenExpiryService {

    @Autowired
    private ApplicationPropertyRepository propertyRepository;

    private static long tokenExpiryTimeInSeconds;
    private static long refreshTokenExpiryTimeInSeconds;
    

    @PostConstruct
    public void init() {
        // Read the token expiry time from the database on application startup
        Optional<ApplicationProperty> tokenExpiryProperty = propertyRepository.findByPropertyName("token_expiry_time");
        Optional<ApplicationProperty> refreshTokenExpiryProperty = propertyRepository.findByPropertyName("refresh_token_expiry_time");

        if (tokenExpiryProperty.isPresent()) {
            tokenExpiryTimeInSeconds = Long.parseLong(tokenExpiryProperty.get().getPropertyValue());
            System.out.println("Token expiry time loaded from DB: " + tokenExpiryTimeInSeconds + " seconds");
        } else {
            // Handle the case where the property is not found in DB
            System.out.println("Token expiry time not found in DB. Using default.");
            tokenExpiryTimeInSeconds = 3600;  // default value in case the property is missing
        }
        
        if (refreshTokenExpiryProperty.isPresent()) {
        	refreshTokenExpiryTimeInSeconds = Long.parseLong(refreshTokenExpiryProperty.get().getPropertyValue());
            System.out.println("Token expiry time loaded from DB: " + refreshTokenExpiryTimeInSeconds + " seconds");
        } else {
            // Handle the case where the property is not found in DB
            System.out.println("Token expiry time not found in DB. Using default.");
            refreshTokenExpiryTimeInSeconds = 604800000;  // default value in case the property is missing
        }
        

        
    }

    public static long getTokenExpiryTime() {
        return tokenExpiryTimeInSeconds;
    }
    public static long getRefreshTokenExpiryTimeInSeconds() {
        return refreshTokenExpiryTimeInSeconds;
    }
}
