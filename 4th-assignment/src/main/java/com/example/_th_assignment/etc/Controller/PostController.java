package com.example._th_assignment.etc.Controller;

import com.example._th_assignment.Dto.CommentDto;
import com.example._th_assignment.Dto.PostDto;
import com.example._th_assignment.Service.CommentService;
import com.example._th_assignment.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public PostController(PostService postService, CommentService commentService) {

        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping
    public String getPosts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());

        return "posts/list";
    }
    @GetMapping("/{id}")
    public String getPost(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.findPostById(id));
        model.addAttribute("comments", commentService.getByPostId(id));
        model.addAttribute("newcomment", new CommentDto());
        return "posts/detail";
    }

    @GetMapping("/form")
    public String getPostForm(Model model) {
        model.addAttribute("postDto", new PostDto());
        return "posts/form";
    }

    @PostMapping
    public String savePost(@ModelAttribute PostDto postDto) {
        postService.savePost(postDto);
        return "redirect:/posts";
    }

    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }

    @GetMapping("/{id}/form")
    public String getEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("postDto", postService.findPostById(id));
        return "posts/form";
    }

    @PutMapping("/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute PostDto postDto) {
//        postService.updatePost(id, postDto);
        return "redirect:/posts/" + id;

    }

}
