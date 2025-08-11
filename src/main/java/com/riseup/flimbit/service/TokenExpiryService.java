package com.riseup.flimbit.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.entity.ApplicationProperty;
import com.riseup.flimbit.entity.SystemSettings;
import com.riseup.flimbit.repository.ApplicationPropertyRepository;
import com.riseup.flimbit.repository.SystemSettingsRepository;

import jakarta.annotation.PostConstruct;

@Service
public class TokenExpiryService {

    @Autowired
    private ApplicationPropertyRepository propertyRepository;
    
    @Autowired
    private SystemSettingsRepository  systemRepository;
    
    
	@Value("${jwt.expiry.time}")
	long expiryTime;
	
	@Value("${jwt.expiry.refresh}")
	long refreshExpiryTime;
	
	@Value("${mobile_auth_token_expiry_time}")
	long mobileAuthTokenExpiryTime;
	
	@Value("${mobile_auth_refresh_token_expiry_time}")
	long mobileAuthRefreshTokenExpiryTime;


    
    /*    private static long tokenExpiryTimeInSeconds;
    private static long refreshTokenExpiryTimeInSeconds;
    
    
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
            System.out.println("Refresh Token expiry time loaded from DB: " + refreshTokenExpiryTimeInSeconds + " seconds");
        } else {
            // Handle the case where the property is not found in DB
            System.out.println("Refresh Token expiry time not found in DB. Using default.");
            refreshTokenExpiryTimeInSeconds = 604800000;  // default value in case the property is missing
        }
        

        
    } */

    public  long getTokenExpiryTime() {
        Optional<ApplicationProperty> tokenExpiryProperty = propertyRepository.findByPropertyName("token_expiry_time");
        if(tokenExpiryProperty.isPresent())
        	System.out.println("reading from  db " + tokenExpiryProperty.get().getPropertyValue());
        long tokenExpiryTimeInSeconds = tokenExpiryProperty.isPresent() ? Long.parseLong(tokenExpiryProperty.get().getPropertyValue()) : expiryTime;
        return tokenExpiryTimeInSeconds;
    }
    public  long getRefreshTokenExpiryTimeInSeconds() {
        Optional<ApplicationProperty> refreshTokenExpiryProperty = propertyRepository.findByPropertyName("refresh_token_expiry_time");
        if(refreshTokenExpiryProperty.isPresent())
        	System.out.println("reading from  db " + refreshTokenExpiryProperty.get().getPropertyValue());
    
        long  refreshTokenExpiryTimeInSeconds = refreshTokenExpiryProperty.isPresent() ? Long.parseLong(refreshTokenExpiryProperty.get().getPropertyValue()) :refreshExpiryTime;
    	
        return refreshTokenExpiryTimeInSeconds;
    }
    public  long getMobileTokenExpiryTime() {
   	    Optional<SystemSettings> sysSettingOPt   =	systemRepository.findByKeyIgnoreCaseAndGroupNameIgnoreCase("mobile_auth_token_expiry_time", EntityName.AUTHENTICATION.name());

        if(sysSettingOPt.isPresent())
        	System.out.println("reading from  db  Mobile Expiry" + sysSettingOPt.get().getValue());
        long tokenExpiryTimeInSeconds = sysSettingOPt.isPresent() ? Long.parseLong(sysSettingOPt.get().getValue()) : mobileAuthTokenExpiryTime;
        return tokenExpiryTimeInSeconds;
    }
    public  long getMobileRefreshTokenExpiryTimeInSeconds() {
   	    Optional<SystemSettings> sysSettingOPt   =	systemRepository.findByKeyIgnoreCaseAndGroupNameIgnoreCase("mobile_auth_refresh_token_expiry_time", EntityName.AUTHENTICATION.name());

        if(sysSettingOPt.isPresent())
        	System.out.println("reading from  db Mobile Refresh " + sysSettingOPt.get().getValue());
    
        long  refreshTokenExpiryTimeInSeconds = sysSettingOPt.isPresent() ? Long.parseLong(sysSettingOPt.get().getValue()) :mobileAuthRefreshTokenExpiryTime;
    	
        return refreshTokenExpiryTimeInSeconds;
    }
    
}
