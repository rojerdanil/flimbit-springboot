package com.riseup.flimbit.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.TokenResponse;
import com.riseup.flimbit.service.TokenExpiryService;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

	@Value("${jwt.secret.key}")
	String SECRET;


	
	@Autowired
	TokenExpiryService tokenExpiryService;

	public String createWebToken(String userDeviceId,boolean isRefreshToken) { // Use email as username
		Map<String, Object> claims = new HashMap<>();
		return createWebTokenAdmin(claims, userDeviceId,isRefreshToken);
	}
	
	

	public String createMobileToken( String key, boolean isRefreshToken) {
	   
		// key is phoneNumber:deviceId
		Map<String, Object> claims = new HashMap<>();

		long timeNew;
		try {
			timeNew = isRefreshToken 
			    ? tokenExpiryService.getMobileTokenExpiryTime() 
			    : tokenExpiryService.getMobileRefreshTokenExpiryTimeInSeconds();
			
			//tokenExpiryService.getMobileTokenExpiryTime();
            //tokenExpiryService.getMobileRefreshTokenExpiryTimeInSeconds();
			String claimType = isRefreshToken ? "refresh" : "access";
		    claims.put("type", claimType);

		    return Jwts.builder()
		        .setClaims(claims)
		        .setSubject(key)
		        .setIssuedAt(new Date())
		        .setExpiration(new Date(System.currentTimeMillis() + timeNew * 1000)) // ✅ FIXED
		        .signWith(getSignKey(), SignatureAlgorithm.HS256)
		        .compact();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // assume both return in seconds


	    return null;
	}

	
	

	private String createWebTokenAdmin(Map<String, Object> claims, String email, boolean isRefreshToken) {
	    long timeNew;
		try {
			timeNew = isRefreshToken 
			    ? tokenExpiryService.getRefreshTokenExpiryTimeInSeconds() 
			    : tokenExpiryService.getTokenExpiryTime();
			
			//tokenExpiryService.getMobileTokenExpiryTime();
            //tokenExpiryService.getMobileRefreshTokenExpiryTimeInSeconds();
			String claimType = isRefreshToken ? "refresh" : "access";
		    claims.put("type", claimType);

		    return Jwts.builder()
		        .setClaims(claims)
		        .setSubject(email)
		        .setIssuedAt(new Date())
		        .setExpiration(new Date(System.currentTimeMillis() + timeNew * 1000)) // ✅ FIXED
		        .signWith(getSignKey(), SignatureAlgorithm.HS256)
		        .compact();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // assume both return in seconds


	    return null;
	}

	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public CommonResponse validateToken(String token, String deviceId, String userName) {
		// key is userName:deviceId

		CommonResponse common = CommonResponse.builder().build();
		try {
			final String username = extractUsername(token);
			System.out.println("username :" + username + " : log :" + userName + ":" + deviceId);
	    	 String tokenKey =userName +":"+ deviceId;

			boolean isValid = (username.equals(tokenKey) && !isTokenExpired(token));
		 System.out.println("is valid "+isValid);
			if (isValid) {
				common.setStatus(Messages.SUCCESS);
				common.setMessage(Messages.JWT_VALID);
			} else {
				if (!username.equals(userName + ":" + deviceId)) {
					common.setStatus(Messages.STATUS_FAILURE);
					common.setMessage(Messages.JWT_TOKEN_DEVICEID_USERNAME_WRONG);
				} else {
					common.setStatus(Messages.STATUS_FAILURE);
					common.setMessage(Messages.JWT_TOKEN_EXPIRED);

				}
			}
		} catch (ExpiredJwtException ex) {
			System.out.println("token expired");
			common.setStatus(Messages.STATUS_FAILURE);
			common.setMessage(Messages.JWT_TOKEN_EXPIRED);
			
			
		} catch (SignatureException ex) {
			common.setStatus(Messages.STATUS_FAILURE);
			common.setMessage(Messages.JWT_TOKEN_INVALID);

		}
		return common;

	}	
		
	
	public boolean validateMobileToken(String token, String deviceId, String phoneNumber) { 
		
		// key is phoneNumber:deviceId

	    try {
	        final String username = extractUsername(token);
	        System.out.println("username: " + username + " : log: " + phoneNumber + " : " + deviceId);

	        String tokenKey = phoneNumber + ":" + deviceId;
	        boolean isValid = (username.equals(tokenKey) && !isTokenExpired(token));
	        System.out.println("is valid: " + isValid);

	        if (!isValid) {
	            if (!username.equals(phoneNumber + ":" + deviceId)) {
	                // Log error message for deviceId and username mismatch
	                System.out.println("Error: " + Messages.JWT_TOKEN_DEVICEID_USERNAME_WRONG);
	            } else {
	                // Log error message for token expiration
	                System.out.println("Error: " + Messages.JWT_TOKEN_EXPIRED);
	            }
	        }

	        return isValid;
	    } catch (ExpiredJwtException ex) {
	        // Log error message for expired token
	        System.out.println("Error: Token expired");
	        return false;
	    } catch (SignatureException ex) {
	        // Log error message for invalid token signature
	        System.out.println("Error: Invalid token signature");
	        return false;
	    }
	}


}
