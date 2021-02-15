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


    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < sysdate() and DATE(p.time) = STR_TO_DATE(:date, '%Y-%m-%d')",
            nativeQuery = true)
    Page<Post> findAllByDate(Pageable pageable, String date);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < sysdate() " +
            "and EXISTS(SELECT 1 FROM tag2post tp, tags t where tp.post_id = p.id and tp.tag_id = t.id and t.name = :tag)",
            nativeQuery = true)
    Page<Post> findAllByTag(Pageable pageable, String tag);


    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = upper(:status)" +
            "and exists (select 1 from users u where u.id = p.moderator_id and u.email = :email)",
            nativeQuery = true)
    Page<Post> findAllForModeration(Pageable pageable, String email, String status);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = :isActive and (:moderationStatus is null or p.moderation_status = :moderationStatus) " +
            "and exists (select 1 from users u where u.id = p.user_id and u.email = :email)",
            nativeQuery = true)
    Page<Post> findMyPosts(Pageable pageable, String email, int isActive, String moderationStatus);


    @Query(value = "select YEAR(p.time) as year, DATE_FORMAT(DATE(p.time), '%Y-%m-%d') as date, count(*) as amount from posts p " +
            "where YEAR(p.time) = :year and p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time < sysdate()  " +
            "group by year, date order by date",
            nativeQuery = true)
    List<CalendarCustom> findPostByYear(int year);

    @Query(value = "select DISTINCT YEAR(p.time) from posts p order by p.time desc", nativeQuery = true)
    List<Integer> findYears();

    @Query(value = "select count(p.id) as postsCount," +
            "       sum((select count(pv.id)" +
            "          from post_votes pv" +
            "         where pv.post_id = p.id" +
            "            and pv.value = 1)) as likesCount," +
            "       sum((select count(pv.id)" +
            "        from post_votes pv" +
            "        where pv.post_id = p.id" +
            "          and pv.value = 0)) as dislikesCount," +
            "       sum(p.view_count) as viewsCount," +
            "       min(p.time) as firstPublication" +
            "  from posts p" +
            "  where p.is_active = 1" +
            "    and p.moderation_status = 'ACCEPTED'" +
            "    and p.time < sysdate()" +
            "    and p.user_id = :userId",
            nativeQuery = true)
    StatisticCustom findByUserId(Long userId);

    @Query(value = "select count(p.id) as postsCount," +
            "       sum((select count(pv.id)" +
            "          from post_votes pv" +
            "         where pv.post_id = p.id" +
            "            and pv.value = 1)) as likesCount," +
            "       sum((select count(pv.id)" +
            "        from post_votes pv" +
            "        where pv.post_id = p.id" +
            "          and pv.value = 0)) as dislikesCount," +
            "       sum(p.view_count) as viewsCount," +
            "       min(p.time) as firstPublication" +
            "  from posts p",
            nativeQuery = true)
    StatisticCustom findAllStatistic();

    @Query(value = "select count(*) from posts p where p.moderation_status = 'NEW' and p.moderator_id is null",
        nativeQuery = true)
    int countPostsForModeration();
}
