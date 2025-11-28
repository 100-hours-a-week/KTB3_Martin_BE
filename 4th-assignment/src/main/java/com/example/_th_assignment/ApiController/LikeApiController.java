package com.example._th_assignment.ApiController;


import com.example._th_assignment.ApiResponse.ApiResponse;
import com.example._th_assignment.Dto.LikeDto;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Service.LikeService;
import com.example._th_assignment.Service.PostService;
import com.example._th_assignment.Service.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
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
    private final PostService postService;
    private final SessionManager sessionManager;

    @Autowired
    public LikeApiController(LikeService likeService, PostService postService, SessionManager sessionManager) {
        this.likeService = likeService;
        this.postService = postService;
        this.sessionManager = sessionManager;
    }

    @GetMapping("/{postid}")
    public ResponseEntity<?> getLikes(
            @PathVariable Long postid, @RequestParam(value = "user", required = false) String email,
            HttpServletRequest request) {
        sessionManager.access2Resource(request);


        if(email ==null) {
            List<LikeDto> list = likeService.getbyPostId(postid);
            return ResponseEntity.ok(ApiResponse.success("get likes success", list));
        }
        LikeDto like = likeService.getbyPostIdAndAuthorEmail(postid, email);
        return ResponseEntity.ok(ApiResponse.success("get like success", like));

    }

    @PostMapping("/{postId}")
    public ResponseEntity<?> saveLike(@PathVariable Long postId, HttpServletRequest request) {
        sessionManager.access2Auth(request);
        UserDto user = (UserDto) request.getSession().getAttribute("user");



        LikeDto like = new LikeDto(postId, user.getEmail());

        like = likeService.saveLike(postId, like);
        URI location = URI.create("/likes/" + postId + "?user=" + like.getAuthorEmail());
        return ResponseEntity.created(location).body(ApiResponse.success("save like success", like));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> deleteLike(@PathVariable Long postId, HttpServletRequest request) {
        sessionManager.access2Resource(request);
        UserDto user = (UserDto) request.getSession().getAttribute("user");


        likeService.deleteLike(postId,user.getEmail());

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/mylike/{postId}")
    public ResponseEntity<?> getLikes(@PathVariable Long postId, HttpServletRequest request) {
        sessionManager.access2Resource(request);
        UserDto user = (UserDto) request.getSession().getAttribute("user");

        String email = user.getEmail();

        boolean exist = likeService.existlike(postId, email);

        return ResponseEntity.ok(exist);

    }


}
