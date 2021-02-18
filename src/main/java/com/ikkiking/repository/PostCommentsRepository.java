package com.ikkiking.repository;

import com.ikkiking.model.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentsRepository extends JpaRepository<PostComments, Long> {

    int countByPostId(Long postId);
    List<PostComments> findAllByPostId(long postId);
}
