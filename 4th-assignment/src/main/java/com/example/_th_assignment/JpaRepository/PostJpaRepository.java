package com.example._th_assignment.JpaRepository;

import com.example._th_assignment.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostJpaRepository extends JpaRepository<Post,Long> {

    @Query("select p from Post p join fetch p.user where p.id = :id and p.isdeleted = false")
    Optional<Post> findByidAndIsdeletedFalse(@Param("id") long postId);
    List<Post> findAllByUser_IdAndIsdeletedFalse(Long id);

    @Query("Select p from Post p join fetch p.user where p.isdeleted = false")
    List<Post> findAllByIsdeletedFalse();
    Long countAllByIsdeletedFalse();
    Boolean existsByidAndIsdeletedFalse(Long id);


    Optional<Post> findByUser_IdAndIsdeletedFalse(long userid);
}
