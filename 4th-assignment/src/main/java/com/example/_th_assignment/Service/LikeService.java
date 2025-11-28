package com.example._th_assignment.Service;

import com.example._th_assignment.CustomException.LikeConflictException;
import com.example._th_assignment.CustomException.LikeNotFoundException;
import com.example._th_assignment.CustomException.PostNotFoundException;
import com.example._th_assignment.CustomException.UserNotFoundException;
import com.example._th_assignment.Dto.LikeDto;
import com.example._th_assignment.Entity.Post;
import com.example._th_assignment.Entity.PostLike;
import com.example._th_assignment.Entity.User;
import com.example._th_assignment.JpaRepository.PostJpaRepository;
import com.example._th_assignment.JpaRepository.PostLikeJpaRepository;
import com.example._th_assignment.JpaRepository.UserJpaRepository;
import com.example._th_assignment.etc.Repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    private final UserJpaRepository userJpaRepository;
    private final PostJpaRepository postJpaRepository;
    private final PostLikeJpaRepository postLikeJpaRepository;

    @Autowired
    public LikeService(UserJpaRepository userJpaRepository,
                       PostJpaRepository postRepository,
                       PostLikeJpaRepository postLikeJpaRepository) {
        this.userJpaRepository = userJpaRepository;
        this.postJpaRepository = postRepository;
        this.postLikeJpaRepository = postLikeJpaRepository;
    }

    public PostLike findByPostIdAndAuthorEmail(long postId, String authorEmail) {
        return postLikeJpaRepository.findByPost_IdAndUser_EmailAndIsdeletedFalse(postId, authorEmail)
                .orElseThrow(() -> new LikeNotFoundException(postId, authorEmail));

    }

    @Transactional(readOnly = true)
    public List<LikeDto> getbyPostId(Long postId) {
        List<PostLike> postLikes = postLikeJpaRepository.findAllWithPost_IdAndIsdeletedFalse(postId);
        List<LikeDto> likeDtos = new ArrayList<>();
        for (PostLike postLike : postLikes) {
            LikeDto likeDto = postLike.toDto();
            likeDtos.add(likeDto);

        }
        return likeDtos;
    }
    @Transactional(readOnly = true)
    public LikeDto getbyPostIdAndAuthorEmail(Long postId, String authorEmail){
        PostLike postLike =  findByPostIdAndAuthorEmail(postId, authorEmail);
        return postLike.toDto();
    }



    @Transactional
    public LikeDto saveLike(Long postid, LikeDto likeDto){
        String email = likeDto.getAuthorEmail();

        Optional <PostLike> existing= postLikeJpaRepository.findByPost_IdAndUser_Email(postid,email);

        if(existing.isPresent()) {
            PostLike like = existing.get();
            if(like.getIsdeleted()) {
                like.refresh();
                return like.toDto();
            }
            throw new LikeConflictException(postid, email);

        }

        Post post = postJpaRepository.findByidAndIsdeletedFalse(postid)
                .orElseThrow(() -> new PostNotFoundException(postid));
        User user = userJpaRepository.findByEmailAndIsdeletedFalse(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        PostLike postLike = new PostLike(post, user);
        return postLikeJpaRepository.save(postLike).toDto();
    }

    @Transactional
    public LikeDto updateLike(Long postid, String authorEmail, LikeDto likeDto){
        return getbyPostIdAndAuthorEmail(postid, authorEmail);

    }

    @Transactional
    public void deleteLike(Long postid, String authorEmail){
        PostLike postLike = findByPostIdAndAuthorEmail(postid, authorEmail);
        postLike.delete();
    }

    @Transactional
    public void deleteAllLike(Long postid){
        List<PostLike> postLikes = postLikeJpaRepository.findAllByPost_IdAndIsdeletedFalse(postid);
        for(PostLike postLike : postLikes){
            postLike.delete();
        }
    }

    @Transactional
    public long countByPostId(Long postid){
        return postLikeJpaRepository.countAllByPost_IdAndIsdeletedFalse(postid);
    }


    public boolean existlike(long postid, String authorEmail){
        return postLikeJpaRepository.existsByPost_IdAndUser_EmailAndIsdeletedFalse(postid, authorEmail);
    }





}
