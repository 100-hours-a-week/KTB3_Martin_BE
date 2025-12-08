package com.example._th_assignment.Service;

import com.example._th_assignment.Dto.Request.RequestUserDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class Validator {

    public void checkValidPassword(RequestUserDto user){
        if(!user.getPassword().equals(user.getCheckingpassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password and checkingpassword are not same");
    }

    public void checkValidNickname(RequestUserDto user){
        String nickname = user.getNickname();
        nickname = nickname.replaceAll(" ", "").toLowerCase();
        String unknown = "unknown";
        if(nickname.equals(unknown))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nickname cannot be unknown");
    }

    public void checkSignUpUser(RequestUserDto user){
        checkValidNickname(user);
        checkValidPassword(user);
    }


}

