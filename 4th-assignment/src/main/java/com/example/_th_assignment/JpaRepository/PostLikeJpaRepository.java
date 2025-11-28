package com.example._th_assignment.JpaRepository;

import com.example._th_assignment.Entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeJpaRepository extends JpaRepository<PostLike, Long> {

    @Query("select pl from PostLike pl " +
            "join fetch pl.post join fetch pl.user " +
            "where pl.post.id =:postId " +
            "and pl.isdeleted= false")
    List<PostLike> findAllWithPost_IdAndIsdeletedFalse(long postId);
    List<PostLike> findAllByPost_IdAndIsdeletedFalse(long postId);

    @Query("select pl from PostLike pl " +
            "join fetch pl.post join fetch pl.user " +
            "where pl.post.id = :postId and pl.user.email = :email " +
            "and pl.post.isdeleted =false and pl.isdeleted =false")
    Optional<PostLike> findByPost_IdAndUser_EmailAndIsdeletedFalse
            (@Param("postId")long postId, @Param("email")String email);

    Boolean existsByPost_IdAndUser_EmailAndIsdeletedFalse(Long postId, String email);
    Boolean existsByIdAndIsdeletedFalse(long id);

    long countAllByPost_IdAndIsdeletedFalse(long postId);

    Optional<PostLike> findByPost_IdAndUser_Email(Long postid, String email);

    @Query("select pl.post.id, count(*) " +
            "from PostLike pl " +
            "where pl.post.id in :postids and pl.isdeleted = false " +
            "group by pl.post.id")
    List<Object[]> countGroupByPost_IdAndIsdeletedFalse(@Param("postids") List<Long> postids);
}
