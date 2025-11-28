package com.example._th_assignment.CustomException;

public class UserNotFoundException extends DtoNotFoundException {
    private String email;
    public UserNotFoundException(String email)
    {
        super("Like Author not found with email:" + email);
        this.email = email;
    }
}
