package com.example._th_assignment.Dto.Response;

import com.example._th_assignment.Dto.JsonViewGroup;
import com.example._th_assignment.Dto.PostDto;
import com.fasterxml.jackson.annotation.JsonView;


public class ResponsePostDto {

    @JsonView(JsonViewGroup.summaryview.class)
    private long id;

    @JsonView(JsonViewGroup.summaryview.class)
    private String title;

    private String content;

    @JsonView(JsonViewGroup.summaryview.class)
    private String author = "unknown";

    @JsonView(JsonViewGroup.summaryview.class)
    private long view;
    @JsonView(JsonViewGroup.summaryview.class)
    private String birthtime;
    @JsonView(JsonViewGroup.summaryview.class)
    private long comments;
    @JsonView(JsonViewGroup.summaryview.class)
    private long likes;

    private String image;

    @JsonView(JsonViewGroup.summaryview.class)
    private String userimage;

    public ResponsePostDto() {}

    public ResponsePostDto(PostDto postDto, long comments, long likes) {

        this.id = postDto.getId();
        this.title = postDto.getTitle();
        this.content = postDto.getContent();
        this.author = postDto.getAuthor();
        this.view = postDto.getViewcount();
        this.birthtime = postDto.getBirthtime();
        this.comments = comments;
        this.likes = likes;
        this.userimage = postDto.getUserimage();
        this.image = postDto.getImage();
    }

    public String getTitle(){
        return title;
    }
    public String getContent(){
        return content;
    }

    public String getAuthor(){
        return author;
    }

    public long getView(){
        return view;
    }

    public String getBirthtime(){
        return birthtime;
    }

    public long getComments(){
        return comments;
    }
    public long getLikes(){
        return likes;
    }
    public String getImage(){
        return image;
    }
    public String getUserimage(){
        return userimage;
    }



}
