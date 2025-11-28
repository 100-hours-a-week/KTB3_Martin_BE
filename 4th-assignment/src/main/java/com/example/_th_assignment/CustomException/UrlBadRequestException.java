package com.example._th_assignment.CustomException;

public class UrlBadRequestException extends RuntimeException{

    private String url;

    public UrlBadRequestException(String url) {

        super("image not found with url:" + url);
        this.url = url;
    }
}
