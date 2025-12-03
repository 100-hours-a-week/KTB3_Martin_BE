package com.example._th_assignment.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;




public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl("/api/user/session");
        setSecurityContextRepository(new HttpSessionSecurityContextRepository());
    }

    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {




        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        try{
            LoginRequestDto loginRequestDto =
                    objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);

            String email = loginRequestDto.getEmail();
            String password = loginRequestDto.getPassword();

            if (email == null || email.isBlank() || password == null || password.isBlank()) {
                throw new AuthenticationServiceException("Email or password is missing");
            }


            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(email, password);
            return this.getAuthenticationManager().authenticate(authRequest);
        }

        catch(IOException e){
            throw new AuthenticationServiceException("Invalid Login Request");
        }
    }


}
