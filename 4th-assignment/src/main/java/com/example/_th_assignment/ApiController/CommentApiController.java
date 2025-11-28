package com.example._th_assignment.ApiController;

import com.example._th_assignment.ApiResponse.ApiResponse;
import com.example._th_assignment.Dto.CommentDto;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Service.AuthorizationManager;
import com.example._th_assignment.Service.CommentService;
import com.example._th_assignment.Service.PostService;
import com.example._th_assignment.Service.SessionManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@Slf4j
public class CommentApiController {
    private final CommentService commentService;
    private final PostService postService;
    private final SessionManager sessionManager;
    private final AuthorizationManager authorizationManager;

    @Autowired
    public CommentApiController(CommentService commentService, PostService postService,
                                SessionManager sessionManager, AuthorizationManager authorizationManager) {
        this.commentService = commentService;
        this.postService = postService;
        this.sessionManager = sessionManager;
        this.authorizationManager = authorizationManager;
    }

    @GetMapping("/{postid}")
    @Operation(summary = "댓글 전체 조회", description = "게시글에 작성된 댓글을 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 필요함")
    })
    public ResponseEntity<Object> getComments(@Parameter(description = "조회할 게시글 id", required = true, example = "1")
                                                  @PathVariable Long postid, HttpServletRequest request){
        sessionManager.access2Resource(request);

        List<CommentDto> list = commentService.getByPostId(postid);


        return ResponseEntity.ok(ApiResponse.success("get all comments success", list));
    }

    @GetMapping("/{postid}/{id}")
    public ResponseEntity<Object> getComment(@PathVariable Long postid,
                                                 @PathVariable Long id, HttpServletRequest request){
        sessionManager.access2Resource(request);
        postService.findPostById(postid);
        CommentDto comment = commentService.getByPostIdAndCommentId(postid, id);

        return ResponseEntity.ok(ApiResponse.success("get comment success", comment));
    }

    @PostMapping("/{postid}")
    public ResponseEntity<Object>  postComment(
            @PathVariable Long postid, @Valid @RequestBody CommentDto comment, HttpServletRequest request){
        sessionManager.access2Auth(request);
        UserDto user = (UserDto) request.getSession().getAttribute("user");



        CommentDto newcomment = commentService.saveComment(comment, user);


        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{postid}/{id}")
                .buildAndExpand(newcomment.getPostid(), newcomment.getId())
                .toUri();



        return ResponseEntity.created(location)
                .body(ApiResponse.success("create comment success", newcomment));
    }

    @PutMapping("/{postid}/{id}")
    public ResponseEntity<Object>  updateComment(
            @PathVariable Long postid, @PathVariable Long id,
            @Valid @RequestBody CommentDto comment, HttpServletRequest request){
        sessionManager.access2Resource(request);
        String writerEmail =commentService.getByPostIdAndCommentId(postid, id).getAuthorEmail();
        authorizationManager.checkAuth(request,writerEmail);


        CommentDto newcomment =  commentService.updateComment(postid,id, comment);

        return ResponseEntity.ok(ApiResponse.success("update comment success", newcomment));
    }

    @DeleteMapping("/{postid}/{id}")
    public ResponseEntity<Map<String, Object>>  deleteComment(
            @PathVariable Long postid, @PathVariable Long id, HttpServletRequest request){
        sessionManager.access2Resource(request);
        String writerEmail = commentService.getByPostIdAndCommentId(postid, id).getAuthorEmail();
        authorizationManager.checkAuth(request,writerEmail);

        commentService.deleteComment(postid, id);

        return ResponseEntity.noContent().build();
    }




}
