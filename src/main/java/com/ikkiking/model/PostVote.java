package com.ikkiking.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity(name = "post_votes")
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull(message = "post_votes.user_id may not be null")
    @Column(name = "user_id")
    private int userId;

    @NotNull(message = "post_votes.post_id may not be null")
    @Column(name = "post_id")
    private int postId;

    @NotNull(message = "post_votes.time may not be null")
    private Date time;

    @NotNull(message = "post_votes.value may not be null")
    private boolean value;

}
