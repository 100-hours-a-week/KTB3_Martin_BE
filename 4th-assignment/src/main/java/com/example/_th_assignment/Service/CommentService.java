package com.example._th_assignment.Service;

import com.example._th_assignment.CustomException.CommentNotFoundException;
import com.example._th_assignment.CustomException.PostNotFoundException;
import com.example._th_assignment.CustomException.UserUnAuthorizedException;
import com.example._th_assignment.Dto.CommentDto;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Entity.Comment;
import com.example._th_assignment.Entity.Post;
import com.example._th_assignment.Entity.User;
import com.example._th_assignment.JpaRepository.CommentJpaRepository;
import com.example._th_assignment.JpaRepository.PostJpaRepository;
import com.example._th_assignment.JpaRepository.UserJpaRepository;
import com.example._th_assignment.etc.Repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {


    private final CommentRepository commentRepository;
    private final UserJpaRepository userJpaRepository;
    private final PostJpaRepository postJpaRepository;
    private final CommentJpaRepository commentJpaRepository;

    @Autowired
    public CommentService(
                          UserJpaRepository userJpaRepository,
                          PostJpaRepository postJpaRepository,
                          CommentJpaRepository commentJpaRepository,
                          CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
        this.userJpaRepository = userJpaRepository;
        this.postJpaRepository = postJpaRepository;
        this.commentJpaRepository = commentJpaRepository;
    }
    public Comment findByPostIdAndId(Long postId, Long commentId) {
        Comment comment = commentJpaRepository.findByPost_IdAndIdAndIsdeletedFalse(postId, commentId)
                .orElseThrow(() -> new CommentNotFoundException(postId, commentId));
        return comment;

    }
    @Transactional(readOnly = true)
    public List<CommentDto> getByPostId(Long postId) {
        List<Comment> commentList = commentJpaRepository.findAllWithPost_IdAndIsdeletedFalse(postId);
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentDto commentDto = comment.toDto();
            commentDtoList.add(commentDto);
        }
        return commentDtoList;
    }

    @Transactional(readOnly = true)
    public CommentDto getByPostIdAndCommentId(Long postId, Long commentId) {
        Comment comment = findByPostIdAndId(postId, commentId);

        return comment.toDto();
    }
    @Transactional
    public CommentDto saveComment(CommentDto commentDto, UserDto userDto) {
        CommentDto newComment = apply2Comment(commentDto, userDto);
        long postId = newComment.getPostid();
        String email = newComment.getAuthorEmail();

        User user = userJpaRepository.findByEmailAndIsdeletedFalse(email)
                .orElseThrow(()-> new UserUnAuthorizedException(email));
        Post post = postJpaRepository.findByidAndIsdeletedFalse(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));
        Comment comment = Comment.from(newComment, user, post);
        comment = commentJpaRepository.save(comment);

        return comment.toDto();
    }

    @Transactional
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentDto) {
        Comment comment = findByPostIdAndId(postId, commentId);

        comment.updateComment(commentDto);



        return comment.toDto();
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        Comment comment = findByPostIdAndId(postId, commentId);
        comment.delete();
    }

    @Transactional
    public void deleteAllComment(Long postId) {
        List<Comment> commentList = commentJpaRepository.findAllByPost_IdAndIsdeletedFalse(postId);

        for (Comment comment : commentList) {
            comment.delete();
            System.out.println(comment.getId() +"-" + comment.isIsdeleted());
        }
    }

    @Transactional(readOnly = true)
    public long countByPostId(Long postId) {
        return commentJpaRepository.countByPost_IdAndIsdeletedFalse(postId);
    }

    public CommentDto apply2Comment(CommentDto requestcomment, UserDto user){
        long id = requestcomment.getId();
        long postid = requestcomment.getPostid();
        String content = requestcomment.getContent();
        String authorEmail = user.getEmail();
        String author = user.getNickname();
        String birthtime = requestcomment.getBirthTime();

        return new CommentDto(id,postid,author,authorEmail,content,birthtime);
    }

    @Transactional(readOnly = true)
    public List<Object[]> countGroupByPostId(List<Long> postIds) {
        return commentJpaRepository.countgroupbypost_id(postIds);
    }


}
