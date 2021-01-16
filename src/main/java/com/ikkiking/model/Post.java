package com.ikkiking.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@Entity(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "posts.is_active may not be null")
    @Column(name = "is_active")
    private boolean isActive;

    @NotNull(message = "posts.moderation_status may not be null")
    @Column(name = "moderation_status")
    @Enumerated(EnumType.STRING)
    private ModerationStatus moderationStatus = ModerationStatus.NEW;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @NotNull(message = "posts.user_id may not be null")
    @JoinColumn(name = "user_id")
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private User user;

    @NotNull(message = "posts.time may not be null")
    private Date time;

    @NotNull(message = "posts.title may not be null")
    private String title;

    @NotNull(message = "posts.text may not be null")
    private String text;

    @NotNull(message = "posts.view_count may not be null")
    @Column(name = "view_count")
    private Long viewCount;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "tag2post",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    private List<Tag> tags;

}
