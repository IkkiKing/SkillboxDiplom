package com.ikkiking.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull(message = "posts.is_active may not be null")
    @Column(name = "is_active")
    private boolean isActive;

    @NotNull(message = "posts.moderation_status may not be null")
    @Column(name = "moderation_status")
    @Enumerated(EnumType.ORDINAL)
    private ModerationStatus moderationStatus = ModerationStatus.NEW;

    @Column(name = "moderator_id")
    private int moderatorId;

    @NotNull(message = "posts.user_id may not be null")
    @Column(name = "user_id")
    private int userId;

    @NotNull(message = "posts.time may not be null")
    private Date time;

    @NotNull(message = "posts.title may not be null")
    private String title;

    @NotNull(message = "posts.text may not be null")
    private String text;

    @NotNull(message = "posts.view_count may not be null")
    @Column(name = "view_count")
    private int viewCount;
}
