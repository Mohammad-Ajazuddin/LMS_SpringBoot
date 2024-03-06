package com.example.leavetracking1.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//T o avoid Cross-Origin Resource Sharing issue. 
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Matches all endpoints starting with /api/
                .allowedOrigins("http://localhost:4200") // Allow requests from Angular app's origin
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow specific HTTP methods
                .allowedHeaders("*"); // Allow all headers
        
        registry.addMapping("/employee/**") // Matches all endpoints starting with /employee/
                .allowedOrigins("http://localhost:4200") // Allow requests from Angular app's origin
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow specific HTTP methods
                .allowedHeaders("*"); // Allow all headers
        
        registry.addMapping("/manager/**") // Matches all endpoints starting with /manager/
                .allowedOrigins("http://localhost:4200") // Allow requests from Angular app's origin
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow specific HTTP methods
                .allowedHeaders("*"); // Allow all headers
        
        registry.addMapping("/**")
        .allowedOrigins("*")
        .allowedMethods("GET", "POST", "PUT", "DELETE")
        .allowedHeaders("*");
    }
}
