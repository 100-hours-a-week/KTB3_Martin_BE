package com.example._th_assignment.ApiController;


import com.example._th_assignment.ApiResponse.ApiResponse;
import com.example._th_assignment.CustomAnnotation.LoginUser;
import com.example._th_assignment.Dto.LikeDto;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Service.LikeService;
import com.example._th_assignment.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
public class LikeApiController {

    private final LikeService likeService;



    @Autowired
    public LikeApiController(LikeService likeService, PostService postService){
        this.likeService = likeService;

    }

    @GetMapping("/{postid}")
    public ResponseEntity<?> getLikes(
            @PathVariable Long postid, @RequestParam(value = "user", required = false) String email) {



        if(email ==null) {
            List<LikeDto> list = likeService.getbyPostId(postid);
            return ResponseEntity.ok(ApiResponse.success("get likes success", list));
        }
        LikeDto like = likeService.getbyPostIdAndAuthorEmail(postid, email);
        return ResponseEntity.ok(ApiResponse.success("get like success", like));

    }

    @PostMapping("/{postId}")
    public ResponseEntity<?> saveLike(@PathVariable Long postId, @LoginUser UserDto user) {


        LikeDto like = likeService.saveLike(postId, user);
        URI location = URI.create("/likes/" + postId + "?user=" + like.getAuthorEmail());
        return ResponseEntity.created(location).body(ApiResponse.success("save like success", like));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> deleteLike(@PathVariable Long postId, @LoginUser UserDto user) {




        likeService.deleteLike(postId,user.getEmail());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mylike/{postId}")
    public ResponseEntity<?> getLikes(@PathVariable Long postId, @LoginUser UserDto user) {

        String email = user.getEmail();
        boolean exist = likeService.existlike(postId, email);
        return ResponseEntity.ok(exist);

    }


}
