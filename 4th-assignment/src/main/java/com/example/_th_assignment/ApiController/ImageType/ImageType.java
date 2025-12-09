package com.example._th_assignment.ApiController.ImageType;

public enum ImageType {
    POSTS,
    PROFILE;

    public static ImageType fromString(String value) {
        return ImageType.valueOf(value.toUpperCase());
    }
}
