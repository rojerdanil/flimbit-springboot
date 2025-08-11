package com.riseup.flimbit.security;

import java.io.IOException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.riseup.flimbit.entity.AdminUser;
import com.riseup.flimbit.entity.User;
import com.riseup.flimbit.service.AdminUserService;
import com.riseup.flimbit.service.UserRegisterService;
import com.riseup.flimbit.serviceImp.AdminUserServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtUserContextFilter extends OncePerRequestFilter {
    private static final String LOGIN_URL_PREFIX = "/login/"; // Your login endpoint
 	Logger logger = LoggerFactory.getLogger(JwtUserContextFilter.class);

	@Autowired
	AdminUserService adminUserService;
	
	@Autowired
	UserRegisterService mobileUserService;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		// Skip JWT validation on login URL
		 return request.getRequestURI().startsWith(LOGIN_URL_PREFIX) ;	
		 
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		
		
		Enumeration<String> headerNames = request.getHeaderNames();
 	  logger.info("*****************Filter starts*********************");
 		if ("OPTIONS".equals(request.getMethod())) {
 			logger.info("OPtion Request so end ");
            filterChain.doFilter(request, response); // Skip further processing for OPTIONS request
            return;
        }
        // Iterate over the header names and print them
     /*   while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println(headerName + ": " + headerValue);
        }
        String fullUrl = request.getRequestURL().toString();
 		String uri = request.getRequestURI();
 		System.out.println("Request URL: " + fullUrl);
 		
       */ 
        String authHeader = request.getHeader("authorization");
 		String deviceType = request.getHeader("x-device-type"); // Identifying phone vs web
 		String deviceId = request.getHeader("x-device-id");
 		String fullUrl = request.getRequestURL().toString();
 		
 // Log URL and URI for debugging
 	/*	System.out.println("Request URL: " + fullUrl);
 		System.out.println(authHeader == null ? "null" : authHeader.startsWith("Bearer "));
 		String accessToken = request.getHeader("accessToken");
 		System.out.println(accessToken == null ? "null" : accessToken);
 		System.out.println(deviceType == null ? "null" : deviceType); 		
 		System.out.println(authHeader == null ? "null" : authHeader.startsWith("Bearer "));*/
 		boolean isPhone = "phone".equalsIgnoreCase(deviceType);
 	/*	System.out.println("Request from device: " + (isPhone ? "Phone" : "Web"));
 		System.out.println("*******************************************"); */

 		


        
 	
 		
        
        try {
        	
        	if (authHeader != null && authHeader.startsWith("Bearer ")) {
        		
        	  
				logger.info("Enter into  authorization is not null : type :" + deviceType + " :isPhone " + isPhone);
				String token = authHeader.substring(7); // Extract token
				UserContext context = null;
				
				if (isPhone) {
					
					logger.info("entering into phone");
			 		String phoneNumber = request.getHeader("phoneNumber");

					User webUser = mobileUserService.validateMobileUserToken(phoneNumber, deviceId, token);
					
					if (webUser != null) {
						context = new UserContext(webUser.getId(), webUser.getPhoneNumber(), webUser.getFirstName(), deviceType,deviceId);
					}
					
				}
				else 
				{
					logger.info("entering into web");

					AdminUser webUser = adminUserService.validateWebTokenFilter(token, deviceId);
					if (webUser != null) {
						context = new UserContext(webUser.getId(), webUser.getPhone(), webUser.getName(), deviceType,deviceId);
					}
					
					

				}
				
				if (context != null) {
					UserContextHolder.setContext(context); // Set the user context
				} else {
					logger.info("Invalid Token or User Not Found ");
					logger.info("Filter url :" + fullUrl);
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token or User Not Found");
					return; // Stop further processing
				}
				
				
        	}
        	else 
        	{
				logger.info("Authorization Header Missing");
				logger.info("Filter url :" + fullUrl);
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization Header Missing");
				return; // Stop further processing
	
        	}
        	
			filterChain.doFilter(request, response); // Continue the filter chain after processing
		} 
        catch (Exception e) {
			logger.info("Authorization Exception occured " );
			logger.info("Filter url :" + fullUrl);
			logger.info(e.getMessage());
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization Header Missing");
			return; // Stop further processing

			
        }
        
        finally {
			UserContextHolder.clear(); // Clean up the user context after request ends
		}
        
        
			}

}
