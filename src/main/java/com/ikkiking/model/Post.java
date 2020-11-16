package com.ikkiking.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    private int viewCount;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "tag2post",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "student_id")}
    )
    private List<Tag> tags;

    @OneToMany(mappedBy = "post_id", fetch = FetchType.LAZY)
    private Collection<PostComments> postComments;

    @OneToMany(mappedBy = "post_id", fetch = FetchType.LAZY)
    private Collection<PostVote> postVotes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Collection<PostComments> getPostComments() {
        return postComments;
    }

    public void setPostComments(Collection<PostComments> postComments) {
        this.postComments = postComments;
    }

    public Collection<PostVote> getPostVotes() {
        return postVotes;
    }

    public void setPostVotes(Collection<PostVote> postVotes) {
        this.postVotes = postVotes;
    }
}
