package com.ikkiking.repository;

import com.ikkiking.model.Post;
import com.ikkiking.model.PostComments;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentsRepository extends CrudRepository<PostComments, Integer> {

    @Query("select count(*) from post_comments pv where pv.post = ?1")
    int countCommentsByPost(Post post);
}
