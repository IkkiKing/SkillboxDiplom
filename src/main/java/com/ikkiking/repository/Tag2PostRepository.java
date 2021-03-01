package com.ikkiking.repository;

import com.ikkiking.model.Tag2Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Tag2PostRepository extends JpaRepository<Tag2Post, Long> {
    void deleteAllByPostId(Long postId);
}
