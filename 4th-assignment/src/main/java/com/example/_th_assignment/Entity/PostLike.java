package com.example._th_assignment.Entity;

import com.example._th_assignment.Dto.LikeDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DialectOverride;

@Entity
@Table(name = "post_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"postid", "userid"}))
@Getter
public class PostLike {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postid")
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private User user;

    @Column(nullable = false)
    private boolean isdeleted = false;




    public PostLike() {}

    public PostLike(Post post, User user) {
        this.post = post;
        this.user = user;
    }

    public static PostLike from(Post post, User user) {
        return new PostLike(post, user);
    }

    public LikeDto toDto() {
        long id = this.id;
        long postid = this.post.getId();
        String authorEmail = this.user.getEmail();

        return new LikeDto(id,postid,authorEmail);
    }

    public void delete(){
        this.isdeleted = true;
    }

    public void refresh(){
        this.isdeleted = false;
    }

    public boolean getIsdeleted() {
        return this.isdeleted;
    }




}
