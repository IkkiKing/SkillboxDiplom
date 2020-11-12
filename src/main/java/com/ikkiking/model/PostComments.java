package com.ikkiking.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity(name = "post_comments")
public class PostComments {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "parent_id")
    private int parentId;

    @NotNull(message = "post_comments.post_id may not be null")
    @Column(name = "post_id")
    private int postId;

    @NotNull(message = "post_comments.user_id may not be null")
    @Column(name = "user_id")
    private int userId;

    @NotNull(message = "post_comments.time may not be null")
    private Date time;

    @NotNull(message = "post_comments.text may not be null")
    private String text;
}
