package com.ikkiking.repository;

import com.ikkiking.model.Post;
import com.ikkiking.model.PostComments;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentsRepository extends CrudRepository<PostComments, Integer> {

    @Query("select count(*) from post_comments pc where pc.post = ?1")
    int countCommentsByPost(Post post);

    @Query(value = "SELECT * FROM post_comments pc " +
            "WHERE pc.post_id = :postId",
            nativeQuery = true)
    List<PostComments> findAllByIPostId(long postId);
}
