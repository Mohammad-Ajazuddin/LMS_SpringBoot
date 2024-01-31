package com.example.leavetracking1.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.leavetracking1.exceptions.APIException;
import com.example.leavetracking1.payload.ResponseOutput;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService customDetailsService;

    // This method is called for each HTTP request to the application
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Extract JWT token from the request
            String token = getToken(request);

            // Validate the token and set authentication in SecurityContext
            if (StringUtils.hasText(token) && !token.isEmpty() && jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);

                // Load user details from the database
                UserDetails userDetails = customDetailsService.loadUserByUsername(email);

                // Create authentication object and set it in SecurityContext
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.info("User authenticated successfully: {}", email);
            }
            else {
            	throw new APIException("Token is empty");
            }

            // Continue with the filter chain
            filterChain.doFilter(request, response);
        } catch (Exception e) {

            logger.error("Authentication failed: {}", e.getMessage());
            
            ResponseOutput responseOutput = new ResponseOutput("failed", null, e.getMessage());
            
            ObjectMapper objectMapper = new ObjectMapper();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String jsonResponse = objectMapper.writeValueAsString(responseOutput);
            response.getWriter().write(jsonResponse);
//            throw new APIException("Unauthorized: " + e.getMessage());
        }
    }

    // Extract the JWT token from the request header
    private String getToken(HttpServletRequest request) {
        try {
        	String token = request.getHeader("Authorization");

            if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
                return token.substring(7); // Remove "Bearer " prefix
            }

            return null;
        }catch(Exception e) {
        	throw new APIException(e.getMessage());
        }
    }
}
