package com.example._th_assignment.Service;

import com.example._th_assignment.CustomException.PostNotFoundException;
import com.example._th_assignment.CustomException.UserUnAuthorizedException;
import com.example._th_assignment.Dto.*;
import com.example._th_assignment.Dto.Request.RequestPostDto;
import com.example._th_assignment.Dto.Response.ResponsePostAndCommentsDto;
import com.example._th_assignment.Dto.Response.ResponsePostDto;
import com.example._th_assignment.Entity.Post;
import com.example._th_assignment.Entity.User;
import com.example._th_assignment.JpaRepository.PostJpaRepository;
import com.example._th_assignment.JpaRepository.PostLikeJpaRepository;
import com.example._th_assignment.JpaRepository.UserJpaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {


    private final PostJpaRepository postJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CommentService commentService;
    private final LikeService likeService;


    @Autowired
    public PostService(PostJpaRepository postJpaRepository,
                       UserJpaRepository userJpaRepository,
                       CommentService commentService,
                       LikeService likeService) {

        this.postJpaRepository = postJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.commentService = commentService;
        this.likeService = likeService;
    }
    public Post findPostById(long id) {
        return postJpaRepository.findByidAndIsdeletedFalse(id)
                .orElseThrow(()-> new PostNotFoundException(id));
    }


    @Transactional
    public PostDto getPostById(long id) {
        Post post = findPostById(id);
        post.plusViewCount();
        return post.toDto();
    }

    @Transactional(readOnly = true)
    public List<PostDto> getAllPosts() {
        List<Post> postList= postJpaRepository.findAllByIsdeletedFalse();
        List<PostDto> postDtoList = new ArrayList<>();
        for(Post post:postList){
            PostDto postDto = post.toDto();
            postDtoList.add(postDto);
        }

        return postDtoList;
    }

    @Transactional
    public PostDto savePost(PostDto postDto) {
        User user = userJpaRepository.findByEmailAndIsdeletedFalse(postDto.getAuthorEmail())
                .orElseThrow(() -> new UserUnAuthorizedException(postDto.getAuthorEmail()));
        Post post = Post.from(postDto, user);
        post = postJpaRepository.save(post);
        return post.toDto();
    }
    @Transactional
    public void deletePost(long id) {
        Post post = findPostById(id);
        post.delete();
        commentService.deleteAllComment(id);
        likeService.deleteAllLike(id);
    }
    @Transactional
    public PostDto updatePost(Long id, PostDto postDto) {
        Post post = findPostById(id);
        post.updatePost(postDto);
        return post.toDto();
    }

    public ResponsePostDto apply2ResponsePostDto(PostDto postDto, long commentnum, long likenum) {
        return new ResponsePostDto(postDto, commentnum, likenum);
    }

    public ResponsePostAndCommentsDto apply2ResponsePostAndCommentsDto(ResponsePostDto responsepost, List<CommentDto> comments) {
        return new ResponsePostAndCommentsDto(responsepost, comments);
    }

    public PostDto apply2PostDto(RequestPostDto requestPostDto, PostDto postDto) {
        Long id = postDto.getId();
        String email = postDto.getAuthorEmail();
        String title = requestPostDto.getTitle();
        String content = requestPostDto.getContent();
        String author = postDto.getAuthor();
        long view = postDto.getViewcount();
        String birthtime = postDto.getBirthtime();
        String image = "";

        if(requestPostDto.getImage()!=null){
            image = requestPostDto.getImage();
        }

        return new PostDto(id,email,title,content,author,view,birthtime,image);

    }






}
