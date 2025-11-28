package com.example._th_assignment.JpaRepository;

import com.example._th_assignment.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c " +
            "join fetch c.post join fetch c.user " +
            "where c.post.id = :postid AND c.id = :id " +
            "AND c.isdeleted=false")
    Optional<Comment> findByPost_IdAndIdAndIsdeletedFalse(@Param("postid")Long postid, @Param("id") long id);

    @Query("select c from Comment c " +
            "join fetch c.post join fetch c.user " +
            "where c.post.id = :postid " +
            "AND c.isdeleted=false AND c.post.isdeleted = false")
    List<Comment> findAllWithPost_IdAndIsdeletedFalse(@Param("postid")Long postid);
    List<Comment> findAllByPost_IdAndIsdeletedFalse(long postid);
    Long countByPost_IdAndIsdeletedFalse(Long postid);
    Optional<Comment> findByIdAndIsdeletedFalse(Long id);


}
