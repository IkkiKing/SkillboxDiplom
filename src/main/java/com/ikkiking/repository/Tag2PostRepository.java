package com.ikkiking.repository;

import com.ikkiking.model.Tag2Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Tag2PostRepository extends JpaRepository<Tag2Post, Long> {
    @Query(value = "select count(*) from  tag2post t2p where t2p.tag_id = :tag_id and t2p.post_id = :post_id",
            nativeQuery = true)
    long countByTagIdAndPostId(@Param("tag_id")long tagId,
                              @Param("post_id")long postId);

    List<Tag2Post> findAllByPostId(Long postId);

    void deleteAllByPostId(Long postId);
    //void saveAll(List<Tag2Post> tag2PostList);
}
