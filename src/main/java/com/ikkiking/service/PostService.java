package com.ikkiking.service;

import com.ikkiking.api.response.PostResponse.*;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PostService {

    public GetPostResponse getPosts(int offset, int limit, String mode) {
        GetPostResponse postResponse = new GetPostResponse();
        createFakeResponse(postResponse);
        return postResponse;
    }

    public SearchPostResponse searchPosts(int offset, int limit, String query){
        SearchPostResponse postResponse = new SearchPostResponse();
        createFakeResponse(postResponse);
        return postResponse;
    }

    public PostByDateResponse getPostsByDate(int offset, int limit, String date){
        PostByDateResponse postResponse = new PostByDateResponse();
        createFakeResponse(postResponse);
        return postResponse;
    }

    public PostByTagResponse getPostsByTag(int offset, int limit, String tag){
        PostByTagResponse postResponse = new PostByTagResponse();
        createFakeResponse(postResponse);
        return postResponse;
    }

    public PostForModerationResponse getPostsForModeration(int offset, int limit, String status){
        PostForModerationResponse postResponse = new PostForModerationResponse();
        createFakeResponse(postResponse);
        return postResponse;
    }

    public MyPostResponse getMyPosts(int offset, int limit, String status){
        MyPostResponse postResponse = new MyPostResponse();
        createFakeResponse(postResponse);
        return postResponse;
    }

    public PostByIdResponse getPostByid(int id){

        User user = new User(1, "Вася Петров");

        Date now = new Date();

        CommentUser commentUser = new CommentUser(1, "Вася Петров", "unknowImage.jpg");

        Comment comment = new Comment(1, now, "SomeTextCOmment", commentUser);

        List<Comment> commenttList = new ArrayList<>();

        commenttList.add(comment);

        List<String> tagList = new ArrayList<>();
        tagList.add(new String("Java"));
        tagList.add(new String("Hadoop"));

        PostByIdResponse postByIdResponse = new PostByIdResponse(1, now,
                true,
                user,
                "Приветсвенный пост",
                "Привет всем, на нашем уютном форуме",
                1,
                2,
                4,
                commenttList,
                tagList
                );

        return postByIdResponse;
    }

    private static void createFakeResponse(PostResponse postResponse){
        postResponse.setCount(5);

        Date now = new Date();

        Post post = new Post(1,
                now,
                new User(1,
                        "Вася Петров"),
                "Приветсвенный пост",
                "Привет всем, на нашем уютном форуме",
                1,
                2,
                3,
                4
        );

        List<Post> listPosts = new ArrayList<>();
        listPosts.add(post);

        postResponse.setPosts(listPosts);
    }


}


