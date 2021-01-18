package com.ikkiking.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity(name = "post_comments")
public class PostComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id")
    private Long parent_id;

    @NotNull(message = "post_comments.post_id may not be null")
    @JoinColumn(name = "post_id")
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Post post;

    @NotNull(message = "post_comments.user_id may not be null")
    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private User user;

    @NotNull(message = "post_comments.time may not be null")
    private Date time;

    @NotNull(message = "post_comments.text may not be null")
    private String text;

}
