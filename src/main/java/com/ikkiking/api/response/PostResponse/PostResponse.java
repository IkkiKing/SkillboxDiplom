package com.ikkiking.api.response.PostResponse;


import java.util.List;

public class PostResponse {
    private Long count;
    private List<PostForResponse> posts;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public List<PostForResponse> getPosts() {
        return posts;
    }

    public void setPosts(List<PostForResponse> posts) {
        this.posts = posts;
    }
}
