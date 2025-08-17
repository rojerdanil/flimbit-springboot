package com.riseup.flimbit.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // allow all endpoints
                .allowedOrigins("http://localhost", "http://localhost:80","https://filmbit-home.onrender.com") // allow all origins (not recommended in production)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "accesstoken", "deviceid", "phonenumber","x-device-id","x-device-type") // Explicitly list headers
                .allowCredentials(true);
    }

}

