package com.ikkiking.api.response.PostResponse;


import java.util.List;

public class PostResponse {
    private Long count;
    private List<Post> posts;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
