package com.example._th_assignment.Service.Mapper;

import com.example._th_assignment.Dto.CommentDto;
import com.example._th_assignment.Dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentDto apply2Comment(CommentDto requestcomment, UserDto user){
        long id = requestcomment.getId();
        long postid = requestcomment.getPostid();
        String content = requestcomment.getContent();
        String authorEmail = user.getEmail();
        String author = user.getNickname();
        String birthtime = requestcomment.getBirthTime();

        return new CommentDto(id,postid,author,authorEmail,content,birthtime);
    }

}
