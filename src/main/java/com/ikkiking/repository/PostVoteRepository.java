package com.ikkiking.repository;


import com.ikkiking.model.Post;
import com.ikkiking.model.PostVote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVoteRepository extends CrudRepository<PostVote, Long> {

    @Query(value = "select count(*) from post_votes pv where pv.post_id = ?1 and pv.value = 1", nativeQuery = true)
    int countLikesByPost(Post post);

    @Query(value = "select count(*) from post_votes pv where pv.post_id = ?1 and pv.value = -1", nativeQuery = true)
    int countDislikesByPost(Post post);
}
