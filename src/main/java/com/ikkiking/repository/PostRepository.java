package com.ikkiking.repository;


import com.ikkiking.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' "
            + "and p.time < sysdate()",
            nativeQuery = true)
    Page<Post> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' "
            + "and p.time < sysdate() "
            + "GROUP BY p.id ORDER BY (select count(*) from post_comments pc where pc.post_id = p.id) desc",
            nativeQuery = true)
    Page<Post> findAllByPopular(Pageable pageable);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' "
            + "and p.time < sysdate() GROUP BY p.id ORDER BY "
            + "(select count(*) from post_votes pv where pv.post_id = p.id and pv.value = 1) desc, "
            + "(select count(*) from post_votes pv where pv.post_id = p.id and pv.value = -1) asc,"
            + " p.view_count desc",
            nativeQuery = true)
    Page<Post> findAllByBest(Pageable pageable);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' "
            + "and p.time < sysdate() and (p.title LIKE %:query% or p.text like %:query%)",
            nativeQuery = true)
    Page<Post> findAllBySearch(Pageable pageable, String query);


    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' "
            + "and p.time < sysdate() and DATE(p.time) = DATE(:date)",
            nativeQuery = true)
    Page<Post> findAllByDate(Pageable pageable, String date);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' "
            + "and p.time < sysdate() "
            + "and EXISTS"
            + "(SELECT 1 FROM tag2post tp, tags t where tp.post_id = p.id and tp.tag_id = t.id and t.name = :tag)",
            nativeQuery = true)
    Page<Post> findAllByTag(Pageable pageable, String tag);


    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = upper(:status)",
            nativeQuery = true)
    Page<Post> findAllForModeration(Pageable pageable, String status);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = upper(:status)"
            + "and exists (select 1 from users u where u.id = p.moderator_id and u.email = :email)",
            nativeQuery = true)
    Page<Post> findAllMyModeration(Pageable pageable, String email, String status);

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = :isActive "
            + "and (:moderationStatus is null or p.moderation_status = :moderationStatus) "
            + "and exists (select 1 from users u where u.id = p.user_id and u.email = :email)",
            nativeQuery = true)
    Page<Post> findMyPosts(Pageable pageable, String email, int isActive, String moderationStatus);


    @Query(value = "select YEAR(p.time) as year, DATE(p.time) as date, "
            + "count(*) as amount from posts p "
            + "where p.is_active = 1 and p.moderation_status = 'ACCEPTED' "
            + "and p.time < sysdate()  "
            + "group by year, date order by p.time",
            nativeQuery = true)
    List<CalendarCustom> findPostByYear(int year);

    @Query(value = "select pp.postsCount, "
            + "       pv.likesCount,"
            + "       pv.dislikesCount,"
            + "       pp.viewsCount,"
            + "       pp.firstPublication"
            + " from (select count(p.id) as postsCount,"
            + "             sum(p.view_count) as viewsCount,"
            + "             min(p.time) as firstPublication"
            + "      from posts p"
            + "      where p.is_active = 1"
            + "        and p.moderation_status = 'ACCEPTED'"
            + "        and p.time < sysdate()"
            + "        and p.user_id = :userId) pp,"
            + "     (select sum(case when pv.value = 1 then 1 else null end) as likesCount, "
            + "             sum(case when pv.value = -1 then 1 else null end) as dislikesCount "
            + "      from post_votes pv"
            + "      where pv.user_id = :userId) pv",
            nativeQuery = true)
    StatisticCustom findMyStatisticByUserId(Long userId);

    @Query(value = "select pp.postsCount, "
            + "       pv.likesCount,"
            + "       pv.dislikesCount,"
            + "       pp.viewsCount,"
            + "       pp.firstPublication"
            + " from (select count(p.id) as postsCount,"
            + "             sum(p.view_count) as viewsCount,"
            + "             min(p.time) as firstPublication"
            + "      from posts p"
            + "      where p.is_active = 1"
            + "        and p.moderation_status = 'ACCEPTED'"
            + "        and p.time < sysdate()) pp,"
            + "     (select sum(case when pv.value = 1 then 1 else null end) as likesCount, "
            + "             sum(case when pv.value = -1 then 1 else null end) as dislikesCount "
            + "      from post_votes pv) pv",
            nativeQuery = true)
    StatisticCustom findAllStatistic();

    @Query(value = "select count(*) from posts p where p.moderation_status = 'NEW' and p.moderator_id is null",
            nativeQuery = true)
    int countPostsForModeration();

    Post findTopByOrderByIdDesc();

    @Query(value = "SELECT * FROM posts p WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' "
            + "and p.time < sysdate() order by p.id desc limit 1",
            nativeQuery = true)
    Post findTop();

}
