package com.example._th_assignment.Service.Mapper;


import com.example._th_assignment.Dto.Request.RequestUserDto;
import com.example._th_assignment.Dto.UserDto;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {
    public UserDto apply2UserForUpdate(RequestUserDto requestUserDto, UserDto user){
        String nickname = requestUserDto.getNickname();
        String email = user.getEmail();
        String password = user.getPassword();
        String image = requestUserDto.getImage();
        return new UserDto(nickname, email, password, image);
    }

    public UserDto apply2User(RequestUserDto requestUserDto) {
        return new UserDto(requestUserDto);
    }





}
