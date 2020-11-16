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
    private PostComments postComments;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PostComments getPostComments() {
        return postComments;
    }

    public void setPostComments(PostComments postComments) {
        this.postComments = postComments;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
