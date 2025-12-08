package com.example._th_assignment.Entity;

import com.example._th_assignment.Dto.UserDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "app_users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String nickname;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String image_path = "";

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;

    private Boolean isdeleted=false;

    @Version
    private long version;

    public User() {
    }


    public User(String nickname, String email, String password, String image_path) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        if (image_path != null) {
            this.image_path = image_path;
        }
    }

    public static User from(UserDto userDto){
        String nickname = userDto.getNickname();
        String email = userDto.getEmail();
        String password = userDto.getPassword();
        String image = userDto.getImage();
        return new User(nickname, email, password, image);
    }

    public UserDto toUserDto(){
        String nickname = this.nickname;
        String email = this.email;
        String password = this.password;
        String image = this.image_path;
        return new UserDto(nickname, email, password, image);
    }

    public void updateUser(UserDto userDto){
        this.nickname = userDto.getNickname();
        this.email = userDto.getEmail();
        this.image_path = userDto.getImage();
    }

    public void changePwd(String newpassword){
        this.password = newpassword;
    }

    public void delete(){
        this.nickname = "Unknown";
        this.email = "deleted_"+ UUID.randomUUID()+"@example.com";
        this.isdeleted=true;
        this.image_path = "";

    }


}
