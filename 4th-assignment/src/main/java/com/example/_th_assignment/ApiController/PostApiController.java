package com.example._th_assignment.ApiController;

import com.example._th_assignment.ApiResponse.ApiResponse;
import com.example._th_assignment.CustomAnnotation.LoginUser;
import com.example._th_assignment.Dto.JsonViewGroup;
import com.example._th_assignment.Dto.PostDto;
import com.example._th_assignment.Dto.Request.RequestPostDto;
import com.example._th_assignment.Dto.Response.ResponsePostAndCommentsDto;
import com.example._th_assignment.Dto.Response.ResponsePostDto;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Security.Authorization.PostAuthorization;
import com.example._th_assignment.Service.*;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/posts")
public class PostApiController {


    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final SessionManager sessionManager;
    private final AuthorizationManager authorizationManager;
    private final PostAuthorization postAuth;

    @Autowired
    public PostApiController(PostService postService, CommentService commentService, LikeService likeService,
                             SessionManager sessionManager, AuthorizationManager authorizationManager, PostAuthorization postAuth) {
        this.postService = postService;
        this.commentService = commentService;
        this.likeService = likeService;
        this.sessionManager = sessionManager;
        this.authorizationManager = authorizationManager;
        this.postAuth = postAuth;
    }

    @GetMapping
    @JsonView(JsonViewGroup.summaryview.class)
    public ResponseEntity<Object> getPosts(HttpServletRequest request) {
        sessionManager.access2Resource(request);


        List<ResponsePostDto> responsePosts = postService.getAllResponsePosts();
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put ("message", "get all posts success");
        response.put("data", responsePosts);







        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPost(@PathVariable long id) {



        ResponsePostAndCommentsDto responsePostAndCommentsDto = postService.getPostAndCommentsDto(id);
        String message = "get post/"+id+ " success";

        return ResponseEntity.ok(ApiResponse.success(message,responsePostAndCommentsDto));
    }

    @PostMapping
    public ResponseEntity<Object> postPost(
            @Valid @RequestBody RequestPostDto requestPostDto, @LoginUser UserDto user){


        PostDto post = postService.postPostDto(requestPostDto, user);


        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(post.getId())
                .toUri();

        return ResponseEntity.created(location).body(ApiResponse.success("save post success", post));

    }

    @PreAuthorize("@postAuth.isOwner(#id, authentication)")
    @PutMapping("/{id}")
    public ResponseEntity<Object> putPost(@PathVariable long id,
            @Valid @RequestBody RequestPostDto requestPostDto){





        PostDto post = postService.updatePost(id, requestPostDto);


        return ResponseEntity.ok(ApiResponse.success("update post success", post));
    }

    @PreAuthorize("@postAuth.isOwner(#id, authentication)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePost(@PathVariable long id){


        postService.deletePost(id);

        return ResponseEntity.noContent().build();


    }



}



