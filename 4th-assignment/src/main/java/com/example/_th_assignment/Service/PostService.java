package com.example._th_assignment.Service;

import com.example._th_assignment.CustomException.PostNotFoundException;
import com.example._th_assignment.CustomException.UserUnAuthorizedException;
import com.example._th_assignment.Dto.CommentDto;
import com.example._th_assignment.Dto.PostDto;
import com.example._th_assignment.Dto.Request.RequestPostDto;
import com.example._th_assignment.Dto.Response.ResponsePostAndCommentsDto;
import com.example._th_assignment.Dto.Response.ResponsePostDto;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Entity.Post;
import com.example._th_assignment.Entity.User;
import com.example._th_assignment.JpaRepository.PostJpaRepository;
import com.example._th_assignment.JpaRepository.UserJpaRepository;
import com.example._th_assignment.Service.Mapper.PostMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
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
    private final FileStorageService fileStorageService;


    public Post findPostById(long id) {
        return postJpaRepository.findByidAndIsdeletedFalse(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }


    @Transactional
    public PostDto getPostById(long id) {
        Post post = findPostById(id);


        return post.toDto();

    }

    @Transactional
    public ResponsePostAndCommentsDto getPostAndCommentsDto(long id) {

        Post post = findPostById(id);
        post.plusViewCount();
        PostDto postDto = post.toDto();

        long commentsnum = commentService.countByPostId((post.getId()));
        long likesnum = likeService.countByPostId((post.getId()));
        ResponsePostDto responsePost = PostMapper.apply2ResponsePostDto(postDto, commentsnum, likesnum);
        List<CommentDto> comments = commentService.getByPostId(id);

        return PostMapper.apply2ResponsePostAndCommentsDto(responsePost, comments);

    }

    @Transactional(readOnly = true)
    public List<PostDto> getAllPosts() {
        List<Post> postList = postJpaRepository.findAllByIsdeletedFalse();
        Collections.reverse(postList);

        return postList.stream().map(Post::toDto).toList();


    }


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
                ));
        ;

        List<ResponsePostDto> responsePosts = new ArrayList<>();
        for (PostDto postDto : postdtos) {
            long commentnum = commentGroup.getOrDefault(postDto.getId(), 0L);
            long likenum = likeGroup.getOrDefault(postDto.getId(), 0L);
            responsePosts.add(PostMapper.apply2ResponsePostDto(postDto, commentnum, likenum));
        }

        return responsePosts;
    }

    @Transactional()
    public PostDto postPostDto(RequestPostDto request, UserDto userDto) {
        PostDto postDto = new PostDto(userDto.getEmail(), userDto.getNickname());
        PostDto post = PostMapper.apply2PostDto(request, postDto);

        return savePost(post);
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
        if(StringUtils.hasText(post.getImageurl())) {
            fileStorageService.deleteImage(post.getImageurl());

        }

    }

    @Transactional
    public PostDto updatePost(Long id, RequestPostDto request) {

        Post post = findPostById(id);
        PostDto postDto = post.toDto();
        postDto = PostMapper.apply2PostDto(request, postDto);
        if(!postDto.getImage().equals(request.getImage())) {
            if(request.getImage() != null) {
                fileStorageService.deleteImage(postDto.getImage());

            }

        }
        post.updatePost(postDto);
        return post.toDto();
    }
}

