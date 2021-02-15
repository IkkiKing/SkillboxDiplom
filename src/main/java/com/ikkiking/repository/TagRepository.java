package com.ikkiking.repository;


import com.ikkiking.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {

    @Override
    Iterable<Tag> findAll();

    @Query(value = "select t.name as name, " +
                          "(select count(*) from tag2post t2p where t2p.tag_id = t.id) / " +
                          "(select count(*) from posts p where p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < sysdate()) as weight " +
                    "from tags t " +
                    "where (:query is null or t.name = :query) group by t.id",
    nativeQuery = true)
    List<TagCustom> findAllByTags(String query);

    @Query(value = "select t.* from tags t, tag2post t2p where t.id = t2p.tag_id and t2p.post_id = :postId",
            nativeQuery = true)
    List<Tag> findAllByPost(Long postId);

    @Query(value = "select t.* from tags t where t.name = :name", nativeQuery = true)
    Optional<Tag> findByName(String name);
}
