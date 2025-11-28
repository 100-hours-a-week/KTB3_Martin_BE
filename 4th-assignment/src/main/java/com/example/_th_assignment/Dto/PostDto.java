package com.example._th_assignment.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;


public class PostDto {


    private Long id;
    @JsonIgnore
    private String authorEmail;


//    @Size(min = 1, max = 26, message = "title is not more than 25",
//            groups = {ValidationGroup.Postnewpost.class})
//    @NotBlank(message = "title should not empty", groups = {ValidationGroup.Postnewpost.class})
    private String title;

//    @NotBlank(message = "content should not empty", groups = {ValidationGroup.Postnewpost.class})
    private String content;

    private String author = "unknown";
    private long viewcount = 0;
    private String birthtime ="";

    private String image = "";

    private String userimage = "";


    @JsonIgnore
    private Boolean isdeleted = false;

    public PostDto() {
    }


    public PostDto(String authorEmail, String author){
        this.authorEmail = authorEmail;
        this.author = author;
    }

    public PostDto(String authorEmail,String title, String content) {
        id = 0L;
        this.authorEmail = authorEmail;
        this.title = title;
        this.content = content;
    }

    @Builder
    public PostDto(Long id, String authorEmail,
                   String title, String content, String author, long viewcount, String birthtime, String image) {
        this.id = id;
        this.authorEmail = authorEmail;
        this.title = title;
        this.content = content;
        this.author = author;
        this.viewcount = viewcount;
        this.birthtime = birthtime;
        this.image = image;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsdeleted() {
        return isdeleted;
    }
    public void setIsdeleted(Boolean isdeleted) {
        this.isdeleted = isdeleted;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public long getViewcount() {
        return viewcount;
    }
    public void setViewcount(long viewcount) {
        this.viewcount = viewcount;
    }
    public String getBirthtime() {
        return birthtime;
    }
    public void setBirthtime(String timeStamp) {
        this.birthtime = timeStamp;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }
    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getUserimage() {
        return userimage;
    }
    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }


}
