package com.example._th_assignment.Service;

import com.example._th_assignment.CustomException.UserFobiddenException;
import com.example._th_assignment.Dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthorizationManager {
    public AuthorizationManager() {}

    public void checkAuth(HttpServletRequest request, String writeremail) {
        UserDto user = (UserDto) request.getSession().getAttribute("user");
        String loggedEmail = user.getEmail();
        if (!loggedEmail.equals(writeremail))
            throw new UserFobiddenException(loggedEmail);
    }

}
