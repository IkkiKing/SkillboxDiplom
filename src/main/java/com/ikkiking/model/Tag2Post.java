package com.ikkiking.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity(name = "tag2post")
public class Tag2Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "tag2post.post_id may not be null")
    @Column(name = "post_id")
    private Long postId;


    @NotNull(message = "tag2post.tag_id may not be null")
    @Column(name = "tag_id")
    private Long tagId;
}
