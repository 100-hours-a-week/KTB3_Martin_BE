package com.example._th_assignment.CustomException;

public class UserFobiddenException extends RuntimeException {
    private String email;
    public UserFobiddenException(String email) {

        super("No permission to perform this operation with email: " + email);
    }
}
