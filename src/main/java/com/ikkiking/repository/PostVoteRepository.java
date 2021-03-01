package com.ikkiking.repository;


import com.ikkiking.model.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, Long> {

    @Query(value = "select (select count(*) from post_votes pv where pv.post_id = :postId and pv.value = 1) as likes, " +
                    "      (select count(*) from post_votes pv where pv.post_id = :postId and pv.value = -1) as dislikes from dual",
                nativeQuery = true)
    Votes getVotes(Long postId);

    Optional<PostVote> findByPostIdAndUserId(Long postId, Long userId);



}

