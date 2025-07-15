package com.riseup.flimbit.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtUserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

/*        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                DecodedJWT jwt = JWT.decode(token); // Use your JWT parser (like Auth0 or JJWT)
                Long userId = jwt.getClaim("userId").asLong();
                String phone = jwt.getClaim("phone").asString();
                String name = jwt.getClaim("name").asString();

                UserContext context = new UserContext();
                context.setUserId(userId);
                context.setPhone(phone);
                context.setName(name);

                UserContextHolder.setContext(context);

            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT");
                return;
            }
        }  */
        
        UserContext context = UserContext.builder().name("admin")
        		.userId(0).phone("9999123").build();
        
       

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear(); // Clean after request ends
        }
    }
}
