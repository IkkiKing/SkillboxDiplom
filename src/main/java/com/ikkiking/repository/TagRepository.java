package com.ikkiking.repository;


import com.ikkiking.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query(value = "select t.name as name, "
            + "(select count(*) from tag2post t2p where t2p.tag_id = t.id) / "
            + "(select count(*) from posts p where p.is_active = 1 "
            + "and p.moderation_status = 'ACCEPTED' and p.time < sysdate()) as weight "
            + "from tags t "
            + "where (:query is null or t.name = :query) group by t.id",
            nativeQuery = true)
    List<TagCustom> findAllByTags(String query);

    List<Tag> findAllByNameIn(List<String> tags);
}
