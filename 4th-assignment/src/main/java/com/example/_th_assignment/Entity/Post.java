package com.example._th_assignment.Entity;

import com.example._th_assignment.Dto.PostDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 26)
    private String title;
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private long viewcount = 0;

    private String imageurl="";

    @CreationTimestamp
    private LocalDateTime createdat;

    @Column(nullable = false)
    private boolean isdeleted = false;

    @Version
    private long version;

    public Post() {}

    public Post(String title, String content, User user, String imageurl) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.imageurl = imageurl;
    }

    public static Post from(PostDto postDto, User user){
        String title = postDto.getTitle();
        String content = postDto.getContent();
        String imageurl = postDto.getImage();
        return new Post(title,content,user,imageurl);
    }

    public PostDto toDto(){
        long id = this.id;
        String authorEmail = user.getEmail();
        String author = user.getNickname();
        String title = this.title;
        String content = this.content;
        String imageurl = this.imageurl;
        long viewcount = this.viewcount;
        String birthtime = this.createdat.format(DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss"));;

        PostDto postdto = PostDto.builder()
                .id(id)
                .authorEmail(authorEmail)
                .author(author)
                .title(title)
                .content(content)
                .image(imageurl)
                .viewcount(viewcount)
                .birthtime(birthtime)
                .build();

        postdto.setUserimage(user.getImage_path());

        return postdto;



    }

    public void updatePost(PostDto post){
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imageurl = post.getImage();
    }

    public void plusViewCount(){
        this.viewcount++;
    }

    public void delete(){
        this.isdeleted=true;
    }




}
