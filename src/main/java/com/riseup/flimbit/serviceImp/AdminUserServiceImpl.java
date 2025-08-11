package com.riseup.flimbit.serviceImp;

import org.springframework.stereotype.Service;

import com.riseup.flimbit.constant.ActionType;
import com.riseup.flimbit.constant.EntityName;
import com.riseup.flimbit.constant.Messages;
import com.riseup.flimbit.controllers.LoginController;
import com.riseup.flimbit.entity.AdminUser;
import com.riseup.flimbit.entity.dto.AdminUserDTO;
import com.riseup.flimbit.repository.AdminUserRepository;
import com.riseup.flimbit.request.AdminUserRequest;
import com.riseup.flimbit.request.LoginRequest;
import com.riseup.flimbit.response.CommonResponse;
import com.riseup.flimbit.response.TokenResponse;
import com.riseup.flimbit.security.UserContextHolder;
import com.riseup.flimbit.service.AdminUserService;
import com.riseup.flimbit.utility.CommonUtilty;
import com.riseup.flimbit.utility.JwtService;
import com.riseup.flimbit.utility.TokenEncryptor;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class AdminUserServiceImpl implements AdminUserService {
	
	Logger logger
    = LoggerFactory.getLogger(AdminUserServiceImpl.class);

    @Autowired
    private AdminUserRepository adminUserRepository;
    
    @Autowired
	private AuditLogServiceImp audit;
    
	@Autowired
	JwtService jwtService;
	
	@Autowired
	TokenEncryptor tokenEncryptor;

    @Override
    public List<AdminUser> getAllAdminUsers() {
        return adminUserRepository.findAll();
    }

    @Transactional
    @Override
    public AdminUser addAdminUser(AdminUserRequest adminUserReq) {
    	adminUserRepository.findByPhoneIgnoreCase(adminUserReq.getPhone())
    	.ifPresent(s -> {
    		throw new RuntimeException("Phone number already available ");
    		
    	});
    	AdminUser adminNew =	new AdminUser() ;
    	 
    	AdminUser admin = adminUserRepository.save(toEntity(adminUserReq,adminNew));
    	 audit.logAction(UserContextHolder.getContext().getUserId(),ActionType.CREATE.name()
 				, EntityName.ADMIN_USER.name(), admin.getId(), "New admin user is added" , adminUserReq);

    	
        return admin;
    }
    
    AdminUser toEntity(AdminUserRequest request,AdminUser admin) {
    	if(CommonUtilty.checkEmptyOrNull(request.getName()))
        admin.setName(request.getName());
    	
    	if(CommonUtilty.checkEmptyOrNull(request.getEmail()))
        admin.setEmail(request.getEmail());
    	
    	if(CommonUtilty.checkEmptyOrNull(request.getPassword()))
         admin.setPasswordHash(request.getPassword());

    	if(request.getRoleId() != 0)
    	admin.setRoleId(request.getRoleId());
    	
    	if(CommonUtilty.checkEmptyOrNull(request.getStatus()))
        admin.setStatus(request.getStatus());
    	
    	if(CommonUtilty.checkEmptyOrNull(request.getPhone()))
        admin.setPhone(request.getPhone());
    	
        admin.setVerified(request.isVerified());
        return admin;
    }

    @Transactional
    @Override
    public AdminUser updateAdminUser(int id, AdminUserRequest adminUserReq) {
        
    	AdminUser adminUser = adminUserRepository.findById(id)
    			 .orElseThrow(() -> new RuntimeException("Admin user not found with id: " + id));
    	
    	
    	 audit.logAction(UserContextHolder.getContext().getUserId(),ActionType.UPDATE.name()
  				, EntityName.ADMIN_USER.name(), adminUser.getId(), "admin user is updated" , adminUser);

    	
    	adminUser = toEntity(adminUserReq, adminUser);
    	
    	
        	
           // adminUser.setId(id);  // Set the ID to update the correct record
            return adminUserRepository.save(adminUser);
    
    }

    @Transactional
    @Override
    public void deleteAdminUser(int id) {
    	AdminUser adminUser = adminUserRepository.findById(id)
   			 .orElseThrow(() -> new RuntimeException("Admin user not found with id: " + id));
        adminUserRepository.deleteById(id);
        audit.logAction(UserContextHolder.getContext().getUserId(),ActionType.DELETE.name()
  				, EntityName.ADMIN_USER.name(), adminUser.getId(), "admin user is deleted" , adminUser);

    
    }

    @Override
    public Optional<AdminUser> getAdminUserById(int id) {
        return adminUserRepository.findById(id);
    }

	

	@Override
	public Page<AdminUserDTO> getAdminUsersWithStatus(int roleId, String status, String searchText, int start,
			int length, String sortColumn, String sortOrder) {
		// TODO Auto-generated method stub
		
		int page = start / length;
		Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortColumn);
		Pageable pageable = PageRequest.of(page, length, sort);

		return adminUserRepository.fetchAllUsersWithStatus(roleId, status, searchText, pageable);
	}
    @Transactional
	@Override
	public TokenResponse loginWithIdentifier(LoginRequest loginRequest) {
		// TODO Auto-generated method stub
		
		Optional<AdminUser> userOpt =  adminUserRepository.findByLoginInput(loginRequest.getUserName(),loginRequest.getPassword());

	    if (userOpt.isPresent()) {
	    	AdminUser user = userOpt.get();
	    	String tokenKey =user.getName() +":"+ loginRequest.getDeviceId();
	    	//System.out.println("Token key :" + tokenKey);
	    	String token = jwtService.createWebToken(tokenKey, false);
	    	//System.out.println("token : " + token);
			String refreshToken = jwtService.createWebToken(tokenKey, true);
			String tokenEnc = tokenEncryptor.encrypt(token);
			String refreshTokenEnc = tokenEncryptor.encrypt(refreshToken);
			user.setToken(tokenEnc);
			user.setRefreshToken(refreshTokenEnc);
			user.setDeviceId(loginRequest.getDeviceId());
			user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			adminUserRepository.save(user);
			
			TokenResponse tokenRes = TokenResponse.builder().accessToken(tokenEnc).refreshToken(refreshTokenEnc)
					.accessTokenExpiry(jwtService.extractExpiration(token).toInstant())
					.refreshTokenExpiry(jwtService.extractExpiration(refreshToken).toInstant()).build();
	    
	    return tokenRes;
	    }
	    
	    
	return null;
	}
    
     
	@Override
	public CommonResponse validateWebToken(String authHeader, String deviceId) {
		// TODO Auto-generated method stub
		
		CommonResponse response = null ;
        try {
         	//System.out.println("token comming");

            // Extract token from "Bearer <token>"
        	 String token = authHeader.replace("Bearer ", "").trim();
         	//System.out.println("token " + token);
         	
         	

             if(token == null || token.trim().length()==0)
                  return null;  
             String decriptedToken = tokenEncryptor.decrypt(token);
             //System.out.println("token : " + token);
             //System.out.println("decriptedToken : " + decriptedToken);
           
        	 
            // Extract username/email/device from token (depending on how you built it
             Optional<AdminUser> adminUerOpt = adminUserRepository.findByDeviceId(deviceId);
             
             if(!adminUerOpt.isPresent())
             {
            	 logger.info("token is not found for device Id " + deviceId);
            	 return null;
             }	 
             else
             {
            	 AdminUser user = adminUerOpt.get();
            	 logger.info("device id  req)" + deviceId + " (db) "+ user.getDeviceId());
            	 String userToken = user.getToken().trim();
            	 String refreshToken = user.getRefreshToken().trim();
            	 String providedToken = token.trim();
            	 
            	// Debug: Print lengths and contents
            	 logger.info("User Token: [" + userToken + "] Length: " + userToken.length());
            	 logger.info("Provided Token: [" + providedToken + "] Length: " + providedToken.length());
            	 logger.info("User Refresh Token: [" + refreshToken + "] Length: " + refreshToken.length());
               
            	


            	 if (!userToken.equals(providedToken) && !refreshToken.equals(providedToken)) {
            		    logger.info("Token does not match with refresh or access token. Device ID: " + deviceId);
            		    return null;
            		}                
            	 logger.info("decreipted logic starts ");
                 String usernameOrEmail = jwtService.extractUsername(decriptedToken);

     	    	 String tokenKey =user.getName() +":"+ deviceId;
                 System.out.println("tokenKey " + tokenKey);

     	    	if(!usernameOrEmail.equalsIgnoreCase(tokenKey))
     	    	{
               	 logger.info("token key is not matched" + deviceId);
               	 return null;

     	    	}
     	    	
     	    	logger.info("Token crossed user db verification " + deviceId);
     	   	     logger.info("Jwt verfication starts " );
                 response = jwtService.validateToken(decriptedToken, deviceId, user.getName());
    	   	     logger.info("Jwt verfication ends" );

 
            	 
             }
             
             
             

            // Call your custom validation method
            
            
	
            	

        } catch (Exception ex) {
        	logger.info("device id error "+deviceId);
        	logger.info(ex.getMessage());
           // response.setStatus("FAILURE");
           // response.setMessage("Invalid or malformed token");
              }

		return response;

	}

	@Override
	public AdminUser validateWebTokenFilter(String authHeader, String deviceId) {
		// TODO Auto-generated method stub
		 CommonResponse response = validateWebToken(authHeader,deviceId);
		 if(response == null || response.getStatus() != Messages.STATUS_SUCCESS)
			 return null;
		 
    	 String token = authHeader.replace("Bearer ", "").trim();

		 
		 String decriptedToken = tokenEncryptor.decrypt(token);
         //System.out.println("token : "	 + token);
         //System.out.println("decriptedToken : " + decriptedToken);
       
    	 
        // Extract username/email/device from token (depending on how you built it
         Optional<AdminUser> adminUerOpt = adminUserRepository.findByDeviceId(deviceId	);

		 
		return adminUerOpt.isPresent() ? adminUerOpt.get() : null;
	}

	@Transactional
	@Override
	public CommonResponse validateRefreshWebToken(String authHeader, String deviceId) {
		
		logger.info("****RefreshToken Token starts ******* ");
		
		CommonResponse response = null ;
        try {
         	//System.out.println("token comming");

            // Extract token from "Bearer <token>"
        	 String token = authHeader.replace("Bearer ", "").trim();
         	//System.out.println("token " + token);
         	
         	

             if(token == null || token.trim().length()==0)
                  return null;  
             String decriptedToken = tokenEncryptor.decrypt(token);
             //System.out.println("token : " + token);
             //System.out.println("decriptedToken : " + decriptedToken);
           
        	 
            // Extract username/email/device from token (depending on how you built it
             Optional<AdminUser> adminUerOpt = adminUserRepository.findByRefreshToken(token);
             
             if(!adminUerOpt.isPresent())
             {
            	 logger.info("token is not found for device Id " + deviceId);
            	 return null;
             }	 
             else
             {
            	 AdminUser user = adminUerOpt.get();
            	 logger.info("device id  req)" + deviceId + " (db) "+ user.getDeviceId());
            	 
            	 if(!user.getDeviceId().trim().equalsIgnoreCase(deviceId.trim()))
            	 {
                	 logger.info("device id is not match" + deviceId);
            		 return null;
            	 }
                 logger.info("decreipted logic starts ");
                 String usernameOrEmail = jwtService.extractUsername(decriptedToken);

     	    	 String tokenKey =user.getName() +":"+ deviceId;
                 System.out.println("tokenKey " + tokenKey);

     	    	if(!usernameOrEmail.equalsIgnoreCase(tokenKey))
     	    	{
               	 logger.info("token key is not matched" + deviceId);
               	 return null;

     	    	}
     	    	
     	    	logger.info("Token crossed user db verification " + deviceId);
     	   	     logger.info("Jwt verfication starts " );
                 response = jwtService.validateToken(decriptedToken, deviceId, user.getName());
    	   	     logger.info("Jwt verfication ends"  + response.getStatus());

                 if(response !=null && response.getStatus().equalsIgnoreCase(Messages.STATUS_SUCCESS))
                 {
                	 logger.info("Refrsh token is valid");
                	/* user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                	 user.setToken(token);
                	 adminUserRepository.save(user);

                	 TokenResponse tokenRes = TokenResponse.builder().accessToken(token).refreshToken(token)
         					.accessTokenExpiry(jwtService.extractExpiration(token).toInstant())
         					.refreshTokenExpiry(jwtService.extractExpiration(token).toInstant()).build();
                	 response.setResult(tokenRes); */
                 }
            	 
             }
             
             
             

            // Call your custom validation method
            
            
	
            	

        } catch (Exception ex) {
        	logger.info("device id error "+deviceId);
        	logger.info(ex.getMessage());
            response.setStatus("FAILURE");
            response.setMessage("Invalid or malformed token");
              }
		logger.info("****RefreshToken Token ends ******* ");

		return response;
	}
}
