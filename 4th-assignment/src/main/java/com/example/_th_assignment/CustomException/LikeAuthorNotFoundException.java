package com.example._th_assignment.CustomException;

public class LikeAuthorNotFoundException extends DtoNotFoundException {
    private String email;
    public LikeAuthorNotFoundException(String email)
    {
        super("Like Author not found with email:" + email);
        this.email = email;
    }
}
