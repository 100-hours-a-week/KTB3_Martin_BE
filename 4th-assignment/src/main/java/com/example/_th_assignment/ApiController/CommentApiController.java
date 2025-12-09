package com.example._th_assignment.ApiController;

import com.example._th_assignment.ApiResponse.ApiResponse;
import com.example._th_assignment.CustomAnnotation.LoginUser;
import com.example._th_assignment.Dto.CommentDto;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Service.CommentService;
import com.example._th_assignment.Service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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



    @Autowired
    public CommentApiController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;


    }

    @GetMapping("/{postid}")
    @Operation(summary = "댓글 전체 조회", description = "게시글에 작성된 댓글을 조회")
    @io.swagger.v3.oas.annotations.responses.ApiResponse
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 필요함")
    })
    public ResponseEntity<Object> getComments(@Parameter(description = "조회할 게시글 id", required = true, example = "1")
                                                  @PathVariable Long postid){


        List<CommentDto> list = commentService.getByPostId(postid);


        return ResponseEntity.ok(ApiResponse.success("get all comments success", list));
    }

    @GetMapping("/{postid}/{id}")
    public ResponseEntity<Object> getComment(@PathVariable Long postid,
                                                 @PathVariable Long id){

        postService.findPostById(postid);
        CommentDto comment = commentService.getByPostIdAndCommentId(postid, id);

        return ResponseEntity.ok(ApiResponse.success("get comment success", comment));
    }

    @PostMapping("/{postid}")
    public ResponseEntity<Object>  postComment(
            @PathVariable Long postid, @Valid @RequestBody CommentDto comment, @LoginUser UserDto user){


        CommentDto newcomment = commentService.saveComment(postid, comment, user);


        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{postid}/{id}")
                .buildAndExpand(newcomment.getPostid(), newcomment.getId())
                .toUri();



        return ResponseEntity.created(location)
                .body(ApiResponse.success("create comment success", newcomment));
    }

    @PreAuthorize("@commentAuth.isOwner(#postid, #id, authentication)")
    @PutMapping("/{postid}/{id}")
    public ResponseEntity<Object>  updateComment(
            @PathVariable Long postid, @PathVariable Long id,
            @Valid @RequestBody CommentDto comment){


        CommentDto newcomment =  commentService.updateComment(postid,id, comment);

        return ResponseEntity.ok(ApiResponse.success("update comment success", newcomment));
    }

    @PreAuthorize("@commentAuth.isOwner(#postid, #id, authentication)")
    @DeleteMapping("/{postid}/{id}")
    public ResponseEntity<Map<String, Object>>  deleteComment(
            @PathVariable Long postid, @PathVariable Long id){

        commentService.deleteComment(postid, id);

        return ResponseEntity.noContent().build();
    }




}
