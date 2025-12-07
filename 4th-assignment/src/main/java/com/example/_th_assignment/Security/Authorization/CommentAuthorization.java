package com.example._th_assignment.Security.Authorization;

import com.example._th_assignment.Security.CustomUserDetails;
import com.example._th_assignment.Service.CommentService;
import com.example._th_assignment.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("commentAuth")
public class CommentAuthorization {

    private final CommentService commentService;

    @Autowired
    public CommentAuthorization(CommentService commentService) {
        this.commentService = commentService;
    }

    public boolean isOwner(Long postId, Long commentId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        String author = commentService.getByPostIdAndCommentId(postId, commentId).getAuthorEmail();

        return email.equals(author);

    }
}