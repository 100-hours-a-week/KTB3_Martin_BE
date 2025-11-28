package com.example._th_assignment.ApiController;

import com.example._th_assignment.ApiResponse.ApiResponse;
import com.example._th_assignment.Dto.*;
import com.example._th_assignment.Dto.Request.RequestPostDto;
import com.example._th_assignment.Dto.Response.ResponsePostAndCommentsDto;
import com.example._th_assignment.Dto.Response.ResponsePostDto;
import com.example._th_assignment.Service.*;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
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

    @Autowired
    public PostApiController(PostService postService, CommentService commentService, LikeService likeService,
                             SessionManager sessionManager, AuthorizationManager authorizationManager) {
        this.postService = postService;
        this.commentService = commentService;
        this.likeService = likeService;
        this.sessionManager = sessionManager;
        this.authorizationManager = authorizationManager;
    }

    @GetMapping
    @JsonView(JsonViewGroup.summaryview.class)
    public ResponseEntity<Object> getPosts(HttpServletRequest request) {
        sessionManager.access2Resource(request);
        List<PostDto> posts = postService.getAllPosts();
        List<ResponsePostDto> responsePosts = new ArrayList<>();
        for (PostDto postDto : posts) {
            long commentnum = commentService.countByPostId((postDto.getId()));
            long likenum = likeService.countByPostId((postDto.getId()));
            responsePosts.add(postService.apply2ResponsePostDto(postDto, commentnum, likenum));
        }
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put ("message", "get all posts success");
        response.put("data", responsePosts);







        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPost(@PathVariable long id, HttpServletRequest request) {
        sessionManager.access2Resource(request);
        PostDto post = postService.getPostById(id);



        long commentsnum = commentService.countByPostId((post.getId()));
        long likesnum = likeService.countByPostId((post.getId()));

        String message = "get post/"+id+ " success";
        ResponsePostDto responsePost = postService.apply2ResponsePostDto(post,commentsnum,likesnum);
        List<CommentDto> comments = commentService.getByPostId(id);

        ResponsePostAndCommentsDto responsePostAndCommentsDto =
                postService.apply2ResponsePostAndCommentsDto(responsePost,comments);

        return ResponseEntity.ok(ApiResponse.success(message,responsePostAndCommentsDto));
    }
    @PostMapping
    public ResponseEntity<Object> postPost(
            @Valid @RequestBody RequestPostDto requestPostDto, HttpServletRequest request){
        HttpSession session = sessionManager.access2Auth(request);
        UserDto user = (UserDto) session.getAttribute("user");

        PostDto post = postService.apply2PostDto(requestPostDto, new PostDto(user.getEmail(), user.getNickname()));
        post = postService.savePost(post);


        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(post.getId())
                .toUri();

        return ResponseEntity.created(location).body(ApiResponse.success("save post success", post));

    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> putPost(@PathVariable long id,
            @Valid @RequestBody RequestPostDto requestPostDto, HttpServletRequest request){
        sessionManager.access2Resource(request);

        UserDto user = (UserDto) request.getSession().getAttribute("user");
        PostDto post = postService.getPostById(id);
        String writerEmail = post.getAuthorEmail();

        authorizationManager.checkAuth(request,writerEmail);

        post = postService.apply2PostDto(requestPostDto, post);
        post = postService.updatePost(id, post);


        return ResponseEntity.ok(ApiResponse.success("update post success", post));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePost(@PathVariable long id, HttpServletRequest request){
        sessionManager.access2Resource(request);
        PostDto post = postService.getPostById(id);

        String writerEmail = post.getAuthorEmail();

        authorizationManager.checkAuth(request,writerEmail);

        postService.deletePost(id);

        return ResponseEntity.noContent().build();


    }



}



