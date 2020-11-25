package com.ikkiking.api.response.PostResponse;

import java.util.Date;

public class Comment {
    private Integer id;
    private Date timestamp;
    private String text;
    private CommentUser user;

    public Comment(Integer id, Date timestamp, String text, CommentUser user) {
        this.id = id;
        this.timestamp = timestamp;
        this.text = text;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public CommentUser getUser() {
        return user;
    }

    public void setUser(CommentUser user) {
        this.user = user;
    }
}
