package com.example._th_assignment.Dto.Request;

import com.example._th_assignment.Dto.ValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUserDto {
    @NotBlank(message= "nickname should not empty",groups = {
            ValidationGroup.Register.class, ValidationGroup.UpdateProperty.class})
    private String nickname;

    @Email(message = "malformed email", groups = {ValidationGroup.Register.class})
    @NotBlank(message= "nickname should not empty",
            groups = {ValidationGroup.Register.class})
    private String email;

    @NotBlank(message= "passwd should not empty" ,
            groups = {ValidationGroup.UpdatePassword.class, ValidationGroup.Register.class})
    @Size(min = 8, max = 20, message = "Passwords are not less than 8 and not more than 20 characters",
            groups = {ValidationGroup.UpdatePassword.class, ValidationGroup.Register.class})

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]).+$",
            message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다.",
            groups = {ValidationGroup.UpdatePassword.class, ValidationGroup.Register.class})
    private String password;

    @NotBlank(message = "image should not empty",
            groups = {ValidationGroup.Register.class, ValidationGroup.UpdateProperty.class})
    private String image = "";

    @NotBlank(message= "checkingpasswd should not empty" ,
            groups = {ValidationGroup.UpdatePassword.class, ValidationGroup.Register.class})
    private String checkingpassword = "";

    public RequestUserDto() {}

    public RequestUserDto(String nickname, String email, String password, String checkingpassword, String image) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.checkingpassword = checkingpassword;
        this.image = image;

    }


}
