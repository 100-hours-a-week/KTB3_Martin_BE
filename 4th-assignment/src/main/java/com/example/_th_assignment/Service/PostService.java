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

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {


    private final PostJpaRepository postJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final CommentService commentService;
    private final LikeService likeService;
    private final SessionManager sessionManager;
    private final AuthorizationManager authorizationManager;


//    @Autowired
//    public PostService(PostJpaRepository postJpaRepository,
//                       UserJpaRepository userJpaRepository,
//                       CommentService commentService,
//                       LikeService likeService) {
//
//        this.postJpaRepository = postJpaRepository;
//        this.userJpaRepository = userJpaRepository;
//        this.commentService = commentService;
//        this.likeService = likeService;
//    }
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

        return postList.stream().map(Post::toDto).toList();
    }

    //TODO: 게시글 전부 가져오고 댓글, 좋아요 그룹화
    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllResponsePosts() {
        List<PostDto> postdtos = getAllPosts();

        List<Long> postIds = postdtos.stream().map(PostDto::getId).toList();
        Map<Long, Long> commentGroup = commentService.countGroupByPostId(postIds)
                .stream().collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
        Map<Long, Long> likeGroup = likeService.countGroupByPostId(postIds)
                .stream().collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));;

        List<ResponsePostDto> responsePosts = new ArrayList<>();
        for (PostDto postDto : postdtos) {
            long commentnum = commentGroup.getOrDefault(postDto.getId(), 0L);
            long likenum = likeGroup.getOrDefault(postDto.getId(), 0L);
            responsePosts.add(apply2ResponsePostDto(postDto, commentnum, likenum));
        }

        return responsePosts;
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
