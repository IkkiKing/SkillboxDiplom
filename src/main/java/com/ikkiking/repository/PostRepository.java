package com.ikkiking.repository;


import com.ikkiking.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < sysdate()",
    nativeQuery = true)
    Page<Post> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < sysdate() GROUP BY p.id ORDER BY (select count(*) from post_comments pc where pc.post_id = p.id) desc",
        nativeQuery = true)
    Page<Post> findAllByPopular(Pageable pageable);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < sysdate() GROUP BY p.id ORDER BY (select count(*) from post_votes pv where pv.post_id = p.id and pv.value = 1) desc",
            nativeQuery = true)
    Page<Post> findAllByBest(Pageable pageable);


    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < sysdate() and p.title LIKE %:query%",
            nativeQuery = true)
    Page<Post> findAllBySearch(Pageable pageable, String query);


    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < sysdate() and date(p.time) = STR_TO_DATE(:date, %Y, %m, %d)",
            nativeQuery = true)
    Page<Post> findAllByDate(Pageable pageable, String date);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < sysdate() " +
            "and EXISTS(SELECT 1 FROM tag2Post tp, tags t where tp.post_id = p.id and tp.tag_id = t.id and t.name = :tag)",
            nativeQuery = true)
    Page<Post> findAllByTag(Pageable pageable, String tag);
}
