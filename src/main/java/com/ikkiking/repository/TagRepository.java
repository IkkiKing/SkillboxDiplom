package com.ikkiking.repository;


import com.ikkiking.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query(value = "select (select count(*)"
            + "        from posts pp"
            + "        where pp.is_active = 1"
            + "          and pp.moderation_status = 'ACCEPTED'"
            + "          and pp.time < sysdate()) as postsCount,"
            + "       count(*) as postsByTagCount, t.name "
            + "from posts p, tag2post t2p, tags t "
            + "where p.is_active = 1"
            + "  and p.moderation_status = 'ACCEPTED'"
            + "  and p.time < sysdate()"
            + "  and p.id = t2p.post_id"
            + "  and t2p.tag_id = t.id"
            + " group by t.id",
            nativeQuery = true)
    List<TagCustom> findAllByTags(String query);

    List<Tag> findAllByNameIn(List<String> tags);
}
