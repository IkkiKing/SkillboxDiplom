package com.ikkiking.service;

import com.ikkiking.api.response.PostResponse.*;

import com.ikkiking.repository.PostCommentsRepository;
import com.ikkiking.repository.PostRepository;
import com.ikkiking.repository.PostVoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostVoteRepository postVoteRepository;
    @Autowired
    private PostCommentsRepository postCommentsRepository;


    private static Page<com.ikkiking.model.Post> getPostFromDb(PostRepository postRepository,
                                                               int limit,
                                                               int offset,
                                                               String mode) {

        Pageable sortedByMode;
        Page<com.ikkiking.model.Post> posts;

        switch (mode) {
            case "early": {
                sortedByMode = PageRequest.of(offset, limit, Sort.by("time").ascending());
                posts = postRepository.findAll(sortedByMode);
                break;
            }
            case "popular": {
                sortedByMode = PageRequest.of(offset, limit);
                posts = postRepository.findAllByPopular(sortedByMode);
                break;
            }
            case "best": {
                sortedByMode = PageRequest.of(offset, limit);
                posts = postRepository.findAllByBest(sortedByMode);
                break;
            }
            //RECENT
            default: {
                sortedByMode = PageRequest.of(offset, limit, Sort.by("time").descending());
                posts = postRepository.findAll(sortedByMode);
                break;
            }
        }
        return posts;
    }

    private static List<Post> getPost(PostVoteRepository postVoteRepository,
                                      PostCommentsRepository postCommentsRepository,
                                      Page<com.ikkiking.model.Post> postPage) {

        List<Post> listPosts = new ArrayList<>();
        postPage.get().forEach(t -> {

            long postId = t.getId();
            int viewCount = t.getViewCount();
            long timestamp = t.getTime().getTime() / 1000L;

            int likesCount = postVoteRepository.countLikesByPost(t);
            int dislikesCount = postVoteRepository.countDislikesByPost(t);
            int commentCount = postCommentsRepository.countCommentsByPost(t);


            String title = t.getTitle();
            String text = t.getText();

            User user = new User(t.getUser().getId(), t.getUser().getName());

            listPosts.add(new Post(postId,
                    timestamp,
                    user,
                    title,
                    text,
                    likesCount,
                    dislikesCount,
                    commentCount,
                    viewCount)
            );
        });

        return listPosts;
    }

    public GetPostResponse getPosts(int limit, int offset, String mode) {

        GetPostResponse postResponse = new GetPostResponse();

        Page<com.ikkiking.model.Post> postPage = getPostFromDb(postRepository, limit, offset, mode);

        List<Post> listPosts = getPost(postVoteRepository, postCommentsRepository, postPage);

        postResponse.setCount(postPage.getTotalElements());
        postResponse.setPosts(listPosts);

        return postResponse;
    }



    public SearchPostResponse searchPosts(int limit, int offset, String query) {
        SearchPostResponse postResponse = new SearchPostResponse();

        Pageable sortedByMode = PageRequest.of(offset, limit, Sort.by("time").descending());

        Page<com.ikkiking.model.Post> postPage = postRepository.findAllBySearch(sortedByMode, query);

        List<Post> listPosts = getPost(postVoteRepository, postCommentsRepository, postPage);

        postResponse.setCount(postPage.getTotalElements());
        postResponse.setPosts(listPosts);

        return postResponse;
    }

    public PostByDateResponse getPostsByDate(int limit, int offset, String date) {
        PostByDateResponse postResponse = new PostByDateResponse();
        createFakeResponse(postResponse);
        return postResponse;
    }

    public PostByTagResponse getPostsByTag(int limit, int offset, String tag) {
        PostByTagResponse postResponse = new PostByTagResponse();
        createFakeResponse(postResponse);
        return postResponse;
    }

    public PostForModerationResponse getPostsForModeration(int limit, int offset, String status) {
        PostForModerationResponse postResponse = new PostForModerationResponse();
        createFakeResponse(postResponse);
        return postResponse;
    }

    public MyPostResponse getMyPosts(int limit, int offset,  String status) {
        MyPostResponse postResponse = new MyPostResponse();
        createFakeResponse(postResponse);
        return postResponse;
    }

    public PostByIdResponse getPostByid(int id) {

        User user = new User(1, "Вася Петров");

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(2020, Calendar.NOVEMBER, 26);
        long date = calendar.getTimeInMillis() / 1000L;


        CommentUser commentUser = new CommentUser(1, "Вася Петров", "unknowImage.jpg");

        Comment comment = new Comment(1, date, "SomeTextCOmment", commentUser);

        List<Comment> commenttList = new ArrayList<>();

        commenttList.add(comment);

        List<String> tagList = new ArrayList<>();
        tagList.add(new String("Java"));
        tagList.add(new String("Hadoop"));

        PostByIdResponse postByIdResponse = new PostByIdResponse(1, date,
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

    private static void createFakeResponse(PostResponse postResponse) {
        postResponse.setCount(5);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(2020, Calendar.NOVEMBER, 26);
        long date = calendar.getTimeInMillis() / 1000L;

        Post post = new Post(1,
                date,
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


