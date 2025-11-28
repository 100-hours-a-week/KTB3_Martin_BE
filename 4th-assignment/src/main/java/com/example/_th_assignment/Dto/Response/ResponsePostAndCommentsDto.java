package com.example._th_assignment.Dto.Response;

import com.example._th_assignment.Dto.CommentDto;
import lombok.Getter;

import java.util.List;

@Getter
public class ResponsePostAndCommentsDto {
    private final ResponsePostDto post;
    private final List<CommentDto> comments;

    public ResponsePostAndCommentsDto(ResponsePostDto responsePostDto, List<CommentDto> comments) {
        this.post = responsePostDto;
        this.comments = comments;
    }
}
