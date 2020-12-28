package com.ikkiking.repository;


import com.ikkiking.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


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


    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < sysdate() and (p.title LIKE %:query% or p.text like %:query%)",
            nativeQuery = true)
    Page<Post> findAllBySearch(Pageable pageable, String query);


    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < sysdate() and date(p.time) = STR_TO_DATE(:date, %Y, %m, %d)",
            nativeQuery = true)
    Page<Post> findAllByDate(Pageable pageable, String date);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < sysdate() " +
            "and EXISTS(SELECT 1 FROM tag2Post tp, tags t where tp.post_id = p.id and tp.tag_id = t.id and t.name = :tag)",
            nativeQuery = true)
    Page<Post> findAllByTag(Pageable pageable, String tag);


    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = upper(:status))",
            nativeQuery = true)
    Page<Post> findAllForModeration(Pageable pageable, String status);

    @Query(value = "select YEAR(p.time) as year, DATE_FORMAT(DATE(p.time), '%Y-%m-%d') as date, count(*) as amount from posts p " +
            "where YEAR(p.time) = :year and p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < sysdate()  " +
            "group by year, date order by date",
            nativeQuery = true)
    List<CalendarCustom> findPostDates(int year);

    @Override
    Optional<Post> findById(Long id);
}
