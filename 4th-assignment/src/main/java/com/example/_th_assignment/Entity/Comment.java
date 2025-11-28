package com.example._th_assignment.Entity;


import com.example._th_assignment.Dto.CommentDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name ="comments")
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postid")
    private Post post;

    @Column(nullable = false)
    private String content;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdat;

    @Column(nullable = false)
    private boolean isdeleted = false;

    @Version
    private long version;

    public Comment(String content, User user, Post post) {
        this.content = content;
        this.user = user;
        this.post = post;
    }

    public Comment(){}

    public static Comment from(CommentDto commentDto, User user, Post post){
        String content = commentDto.getContent();
        return new Comment(content,user,post);
    }

    public CommentDto toDto(){
        long id = this.id;
        long postid = this.post.getId();
        String author = this.user.getNickname();
        String authorEmail = this.user.getEmail();
        String content = this.content;
        String birthtime = this.createdat.format(DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss"));;
        CommentDto comment =  CommentDto.builder()
                .id(id)
                .postid(postid)
                .author(author)
                .authorEmail(authorEmail)
                .content(content)
                .birthTime(birthtime)
                .build();

        comment.setUserimage(user.getImage_path());

        return comment;


    }

    public void updateComment(CommentDto commentDto){
        this.content = commentDto.getContent();
    }

    public void delete(){
        this.isdeleted = true;
    }


}
