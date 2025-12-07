package com.example._th_assignment.Security.Authorization;


import com.example._th_assignment.Dto.PostDto;
import com.example._th_assignment.Security.CustomUserDetails;
import com.example._th_assignment.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("postAuth")
public class PostAuthorization {

    private final PostService postService;

    @Autowired
    public PostAuthorization(PostService postService) {
        this.postService = postService;
    }

    public boolean isOwner(Long postId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        String author = postService.getPostById(postId).getAuthorEmail();

        return email.equals(author);

    }
}
