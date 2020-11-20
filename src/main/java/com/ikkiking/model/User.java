package com.ikkiking.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotNull(message = "users.is_moderator may not be null")
    @Column(name = "is_moderator")
    private boolean isModerator;

    @NotNull(message = "users.reg_time may not be null")
    @Column(name = "reg_time")
    private Date regTime;

    @NotNull(message = "users.name may not be null")
    private String name;

    @NotNull(message = "users.email may not be null")
    private String email;

    @NotNull(message = "users.password may not be null")
    private String password;

    private String code;

    private String text;

    /*@OneToMany(mappedBy = "user_id", fetch = FetchType.LAZY)
    private Collection<Post> posts;

    @OneToMany(mappedBy = "user_id", fetch = FetchType.LAZY)
    private Collection<PostComments> postComments;

    @OneToMany(mappedBy = "user_id", fetch = FetchType.LAZY)
    private Collection<PostVote> postVotes;*/

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public boolean isModerator() {
        return isModerator;
    }

    public void setModerator(boolean moderator) {
        isModerator = moderator;
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /*public Collection<Post> getPosts() {
        return posts;
    }

    public void setPosts(Collection<Post> posts) {
        this.posts = posts;
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
    }*/
}
