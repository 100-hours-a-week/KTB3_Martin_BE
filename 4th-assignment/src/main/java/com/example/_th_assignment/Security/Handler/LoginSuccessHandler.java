package com.example._th_assignment.Security.Handler;

import com.example._th_assignment.ApiResponse.ApiResponse;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
//        UserDto user = (UserDto) authentication.getPrincipal();
//
//        Authentication auth = authentication;
//        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
//        securityContext.setAuthentication(auth);
//
//        HttpSession session = request.getSession(true); // 세션 생성
//        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

//        System.out.println("Inside loginSuccessHandler.onAuthenticationSuccess()");

        CustomUserDetails customUserDetails =  (CustomUserDetails) authentication.getPrincipal();

        UserDto user = customUserDetails.getUser();



        ApiResponse<?> apiResponse = ApiResponse.success("login success in principal", user);

        String reponseJson = objectMapper.writeValueAsString(apiResponse);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(reponseJson);



    }

}
