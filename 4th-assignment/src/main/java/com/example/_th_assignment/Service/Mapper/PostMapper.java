package com.example._th_assignment.Service.Mapper;

import com.example._th_assignment.Dto.CommentDto;
import com.example._th_assignment.Dto.PostDto;
import com.example._th_assignment.Dto.Request.RequestPostDto;
import com.example._th_assignment.Dto.Response.ResponsePostAndCommentsDto;
import com.example._th_assignment.Dto.Response.ResponsePostDto;

import java.util.List;

public class PostMapper {
    public static ResponsePostDto apply2ResponsePostDto(PostDto postDto, long commentnum, long likenum) {
        return new ResponsePostDto(postDto, commentnum, likenum);
    }

    public static ResponsePostAndCommentsDto apply2ResponsePostAndCommentsDto(ResponsePostDto responsepost, List<CommentDto> comments) {
        return new ResponsePostAndCommentsDto(responsepost, comments);
    }

    public static PostDto apply2PostDto(RequestPostDto requestPostDto, PostDto postDto) {
        Long id = postDto.getId();
        String email = postDto.getAuthorEmail();
        String title = requestPostDto.getTitle();
        String content = requestPostDto.getContent();
        String author = postDto.getAuthor();
        long view = postDto.getViewcount();
        String birthtime = postDto.getBirthtime();
        String image = "";

        if(requestPostDto.getImage()!=null){
            image = requestPostDto.getImage();
        }

        return new PostDto(id,email,title,content,author,view,birthtime,image);

    }


}
