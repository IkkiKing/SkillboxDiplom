package com.ikkiking.repository;


import com.ikkiking.model.PostVote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVoteRepository extends CrudRepository<PostVote, Integer> {
}
