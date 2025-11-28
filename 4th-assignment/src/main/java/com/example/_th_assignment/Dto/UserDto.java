package com.example._th_assignment.Dto;

import com.example._th_assignment.Dto.Request.RequestUserDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class UserDto {
    private String nickname;


    @NotBlank(message= "email should not empty" ,
            groups = {ValidationGroup.Login.class})
    private String email;

    @NotBlank(message= "passwd should not empty" ,
            groups = {ValidationGroup.Login.class})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String imageurl = "";

    public UserDto() {

    }


    public UserDto(RequestUserDto registerUser) {
        this.nickname = registerUser.getNickname();
        this.email = registerUser.getEmail();
        this.password = registerUser.getPassword();
        if(registerUser.getImage()!=null) {
            this.imageurl = registerUser.getImage();
        }
    }


    public UserDto( String nickname, String email, String password, String imageurl) {

        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.imageurl = imageurl;
    }

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }

    public String getImageurl() {
        return imageurl;
    }
    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }



}
