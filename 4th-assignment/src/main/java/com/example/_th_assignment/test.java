package com.example._th_assignment;

import com.example._th_assignment.Dto.Request.RequestUserDto;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class test {




    RequestUserDto createRequestUser(String nickname){

        return RequestUserDto.builder()
                .nickname(nickname)
                .email("e@example.com")
                .password("Mypassword1!")
                .checkingpassword("Mypassword1!")
                .image("image")
                .build();
    }

    RequestUserDto createRequestForPasswordUpdate(String password){

        return RequestUserDto.builder()
                .nickname("nickname")
                .email("e@example.com")
                .password(password)
                .checkingpassword(password)
                .image("image")
                .build();
    }
    RequestUserDto createRequestForPasswordUpdate(String password, String checkingpassword){

        return RequestUserDto.builder()
                .nickname("nickname")
                .email("e@example.com")
                .password(password)
                .checkingpassword(checkingpassword)
                .image("image")
                .build();
    }



    UserDto createUser(String nickname) {
        UserDto oldUserProperty = new UserDto(nickname,
                "exam@example.com222",
                "Mypassword1!",
                "img_url333");
        return oldUserProperty;

    }

    void createAuth(UserDto user){
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }




}
