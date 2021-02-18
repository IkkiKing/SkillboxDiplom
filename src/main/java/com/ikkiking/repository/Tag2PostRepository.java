package com.ikkiking.repository;

import com.ikkiking.model.Tag2Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Tag2PostRepository extends JpaRepository<Tag2Post, Long> {
    long countByTagIdAndPostId(Long tagId, Long postId);
    List<Tag2Post> findAllByPostId(Long postId);
    void deleteAllByPostId(Long postId);
}
