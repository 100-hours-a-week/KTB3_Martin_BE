package com.example._th_assignment.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RequestPostDto {

    @Size(min = 1, max = 26, message = "title is not more than 25")
    @NotBlank(message = "post title should not empty")
    private String title;

    @NotBlank(message = "post content should not empty")
    private String content;

    private String image;

    public RequestPostDto() {}

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }


}
