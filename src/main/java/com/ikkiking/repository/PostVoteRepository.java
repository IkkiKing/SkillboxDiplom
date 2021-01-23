package com.ikkiking.repository;


import com.ikkiking.model.Post;
import com.ikkiking.model.PostVote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVoteRepository extends CrudRepository<PostVote, Long> {

    @Query(value = "select count(*) from post_votes pv where pv.post_id = ?1 and pv.value = 1", nativeQuery = true)
    int countLikesByPost(Post post);

    @Query(value = "select count(*) from post_votes pv where pv.post_id = ?1 and pv.value = -1", nativeQuery = true)
    int countDislikesByPost(Post post);

    @Query(value = "select * from post_votes pv where pv.post_id = :postId and pv.user_id = :userId", nativeQuery = true)
    Optional<PostVote> findByPostIdAndUserId(Long postId, Long userId);



}

