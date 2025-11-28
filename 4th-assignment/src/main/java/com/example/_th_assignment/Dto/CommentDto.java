package com.example._th_assignment.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {

    private long id;
    private long postid;
    private String author = "unknown";
    @JsonIgnore
    private String authorEmail = "unknown";
    @NotBlank(message = "comment content should not empty")
    private String content;
    private String birthTime;
    private String userimage;

    @JsonIgnore
    private Boolean isdeleted = false;

    public CommentDto() {
    }
    public CommentDto(Long postID, String content, String authorEmail) {
        this.postid = postID;
        this.content = content;
        this.authorEmail = authorEmail;
    }

    @Builder
    public CommentDto(Long id, Long postid, String author, String authorEmail, String content, String birthTime) {
        this.id = id;
        this.postid = postid;
        this.author = author;
        this.authorEmail = authorEmail;
        this.content = content;
        this.birthTime = birthTime;
    }


}
