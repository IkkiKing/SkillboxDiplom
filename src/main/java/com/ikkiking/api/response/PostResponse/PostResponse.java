package com.ikkiking.api.response.PostResponse;


import java.util.List;

public class PostResponse {
    private long count;
    private List<Post> posts;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
