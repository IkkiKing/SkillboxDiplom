package com.ikkiking.repository;

import com.ikkiking.model.PostComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentsRepository extends JpaRepository<PostComments, Long> {

}
