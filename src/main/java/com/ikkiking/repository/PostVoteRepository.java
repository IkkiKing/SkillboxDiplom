package com.ikkiking.repository;


import com.ikkiking.model.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, Long> {

    @Query(value = "select count(*) from post_votes pv where pv.post_id = :postId and pv.value = 1", nativeQuery = true)
    int countLikesByPostId(Long postId);

    @Query(value = "select count(*) from post_votes pv where pv.post_id = :postId and pv.value = -1", nativeQuery = true)
    int countDislikesByPostId(Long postId);

    Optional<PostVote> findByPostIdAndUserId(Long postId, Long userId);



}

