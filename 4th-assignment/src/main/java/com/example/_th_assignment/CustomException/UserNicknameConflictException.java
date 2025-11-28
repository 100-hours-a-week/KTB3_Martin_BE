package com.example._th_assignment.CustomException;

public class UserNicknameConflictException extends DtoConflictException {
    private final String nickname;

    public UserNicknameConflictException(String nickname){
        super("User conflict with nickname:" + nickname);
        this.nickname = nickname;
    }

    public String explain(){
        return "same nickname already exists";
    }
}
