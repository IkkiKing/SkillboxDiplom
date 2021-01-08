package com.ikkiking.service;

import com.ikkiking.api.response.PostResponse.*;
import com.ikkiking.model.Post;
import com.ikkiking.model.PostComments;
import com.ikkiking.model.Tag;
import com.ikkiking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostVoteRepository postVoteRepository;
    @Autowired
    private PostCommentsRepository postCommentsRepository;
    @Autowired
    private TagRepository tagRepository;

    private static Page<Post> getPostFromDb(PostRepository postRepository,
                                                               int limit,
                                                               int offset,
                                                               String mode) {

        Pageable sortedByMode;
        Page<Post> posts;

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


    private static void enrichPost(PostVoteRepository postVoteRepository,
                                   PostCommentsRepository postCommentsRepository,
                                   Page<Post> postPage,
                                   @NotNull PostResponse postResponse) {

        List<PostForResponse> listPosts = new ArrayList<>();
        long elementsCount = 0;

        //Нужна ли проверка на null? Судя по всему обращение в бд, даже если ничего не находится
        //Инициализирует коллекцию
        if (postPage != null) {
            elementsCount = postPage.getTotalElements();
            postPage.get().forEach(t -> {

                long postId = t.getId();
                long viewCount = t.getViewCount();
                long timestamp = t.getTime().getTime() / 1000L;

                long likesCount = postVoteRepository.countLikesByPost(t);
                long dislikesCount = postVoteRepository.countDislikesByPost(t);
                long commentCount = postCommentsRepository.countCommentsByPost(t);

                String title = t.getTitle();
                String text = t.getText();

                User user = new User(t.getUser().getId(), t.getUser().getName());

                listPosts.add(new PostForResponse(postId,
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
        }
        postResponse.setCount(elementsCount);
        postResponse.setPosts(listPosts);
    }

    public GetPostResponse getPosts(int limit, int offset, String mode) {

        Page<Post> postPage = getPostFromDb(postRepository, limit, offset, mode);

        GetPostResponse postResponse = new GetPostResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return postResponse;
    }


    public SearchPostResponse searchPosts(int limit, int offset, String query) {

        Pageable sortedByMode = PageRequest.of(offset, limit, Sort.by("time").descending());

        Page<Post> postPage = postRepository.findAllBySearch(sortedByMode, query);

        SearchPostResponse postResponse = new SearchPostResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return postResponse;
    }

    public PostByDateResponse getPostsByDate(int limit, int offset, String date) {

        Pageable sortedByMode = PageRequest.of(offset, limit, Sort.by("time").descending());

        Page<Post> postPage = postRepository.findAllByDate(sortedByMode, date);

        PostByDateResponse postResponse = new PostByDateResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return postResponse;
    }

    public PostByTagResponse getPostsByTag(int limit, int offset, String tag) {


        Pageable sortedByMode = PageRequest.of(offset, limit, Sort.by("time").descending());

        Page<Post> postPage = postRepository.findAllByTag(sortedByMode, tag);

        PostByTagResponse postResponse = new PostByTagResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return postResponse;
    }

    public PostForModerationResponse getPostsForModeration(int limit, int offset, String status) {

        Pageable sortedByMode = PageRequest.of(offset, limit, Sort.by("time").descending());

        Page<Post> postPage = postRepository.findAllForModeration(sortedByMode, status);

        PostForModerationResponse postResponse = new PostForModerationResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return postResponse;
    }

    public MyPostResponse getMyPosts(int limit, int offset, String status) {
        MyPostResponse postResponse = new MyPostResponse();
        createFakeResponse(postResponse);
        return postResponse;
    }

    public PostByIdResponse getPostByid(long id) {
        PostByIdResponse postByIdResponse = null;

        Optional<Post> postOptional = postRepository.findById(id);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();

            Long postId      = post.getId();
            long timestamp   = post.getTime().getTime() / 1000L;
            boolean isActive = post.isActive();


            User user = new User(post.getUser().getId(), post.getUser().getName());
            String title = post.getTitle();
            String text = post.getText();
            long likesCount = postVoteRepository.countLikesByPost(post);
            long dislikesCount = postVoteRepository.countDislikesByPost(post);
            long viewCount = post.getViewCount();


            List<PostComments> postCommentsList = postCommentsRepository.findAllByIPostId(postId);
            List<Comment> commentList = new ArrayList<>();

            postCommentsList.forEach(a -> {

                CommentUser commentUser = new CommentUser(a.getUser().getId(),
                        a.getUser().getName(),
                        a.getUser().getPhoto());

                Comment comment = new Comment(a.getId(),
                        a.getTime().getTime() / 1000L,
                        a.getText(),
                        commentUser);

                commentList.add(comment);
            });

            List<Tag> tagList = tagRepository.findAllByPost(postId);

            Set<String> tagStrList = new HashSet<>();

            //Тот же самый вопрос, нужна ли действительно проверка на null?
            if (tagList != null) {
                tagList.forEach(a -> {
                    tagStrList.add(a.getName());
                });

            }

            postByIdResponse = new PostByIdResponse(post.getId(),
                    post.getTime().getTime() / 1000L,
                    post.isActive(),
                    user,
                    title,
                    text,
                    likesCount,
                    dislikesCount,
                    viewCount,
                    commentList,
                    tagStrList
            );
        }

        return postByIdResponse;
    }

    private static void createFakeResponse(PostResponse postResponse) {
        /*postResponse.setCount(5);

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

        postResponse.setPosts(listPosts);*/
    }


}


