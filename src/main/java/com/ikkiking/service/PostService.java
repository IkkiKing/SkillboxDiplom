package com.ikkiking.service;


import com.ikkiking.api.request.PostRequest;
import com.ikkiking.api.response.PostResponse.*;
import com.ikkiking.model.*;
import com.ikkiking.repository.*;
import com.ikkiking.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.*;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;
    private final PostCommentsRepository postCommentsRepository;
    private final TagRepository tagRepository;
    private final Tag2PostRepository tag2PostRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostService(PostRepository postRepository, PostVoteRepository postVoteRepository, PostCommentsRepository postCommentsRepository, TagRepository tagRepository, Tag2PostRepository tag2PostRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.postCommentsRepository = postCommentsRepository;
        this.tagRepository = tagRepository;
        this.tag2PostRepository = tag2PostRepository;
        this.userRepository = userRepository;
    }

    //Получение постов по переданному типу
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

    //Процедура обогащения поста
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

                UserResponse user = new UserResponse(t.getUser().getId(), t.getUser().getName());

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

    //Получение постов
    public GetPostResponse getPosts(int limit, int offset, String mode) {

        Page<Post> postPage = getPostFromDb(postRepository, limit, offset, mode);

        GetPostResponse postResponse = new GetPostResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return postResponse;
    }

    //Получение списка постов по строке поиска
    public SearchPostResponse searchPosts(int limit, int offset, String query) {

        Pageable sortedByMode = PageRequest.of(offset, limit, Sort.by("time").descending());

        Page<Post> postPage = postRepository.findAllBySearch(sortedByMode, query);

        SearchPostResponse postResponse = new SearchPostResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return postResponse;
    }

    //Получение постов по указанной дате
    public PostByDateResponse getPostsByDate(int limit, int offset, String date) {

        Pageable sortedByMode = PageRequest.of(offset, limit, Sort.by("time").descending());

        Page<Post> postPage = postRepository.findAllByDate(sortedByMode, date);

        PostByDateResponse postResponse = new PostByDateResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return postResponse;
    }

    //Получение постов по тэгу
    public PostByTagResponse getPostsByTag(int limit, int offset, String tag) {

        Pageable sortedByMode = PageRequest.of(offset, limit, Sort.by("time").descending());

        Page<Post> postPage = postRepository.findAllByTag(sortedByMode, tag);

        PostByTagResponse postResponse = new PostByTagResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return postResponse;
    }

    //Получение постов для модерации
    public PostForModerationResponse getPostsForModeration(int limit, int offset, String status) {

        Pageable sortedByMode = PageRequest.of(offset, limit, Sort.by("time").descending());

        String email = SecurityUser.getEmailFromContext();

        Page<Post> postPage = postRepository.findAllForModeration(sortedByMode, email, status);

        PostForModerationResponse postResponse = new PostForModerationResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return postResponse;
    }

    //Получение списка своих постов
    public MyPostResponse getMyPosts(int limit, int offset, String status) {

        Pageable sortedByMode = PageRequest.of(offset, limit, Sort.by("time").descending());

        String email = SecurityUser.getEmailFromContext();

        int isActive = 0;
        String moderationStatus = null;

        switch (status) {
            case "inactive": {
                break;
            }
            case "pending": {
                isActive = 1;
                moderationStatus = "NEW";
                break;
            }
            case "declined": {
                isActive = 1;
                moderationStatus = "DECLINED";
                break;
            }
            case "published": {
                isActive = 1;
                moderationStatus = "ACCEPTED";
                break;
            }
        }
        //System.out.println(email + " " + isActive + " " +moderationStatus);
        Page<Post> postPage = postRepository.findMyPosts(sortedByMode, email, isActive, moderationStatus);

        MyPostResponse postResponse = new MyPostResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return postResponse;
    }

    //Получение поста по ID
    public PostByIdResponse getPostByid(long id) {
        PostByIdResponse postByIdResponse = null;

        Optional<Post> postOptional = postRepository.findById(id);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();

            Long postId = post.getId();
            long timestamp = post.getTime().getTime() / 1000L;
            boolean isActive = post.isActive();


            UserResponse user = new UserResponse(post.getUser().getId(), post.getUser().getName());
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


            //Увеличиваем кол-во просмотров на 1
            if (isIncrementViewCount(post.getUser())) {
                post.setViewCount(post.getViewCount() + 1);
                postRepository.save(post);
            }
        }

        return postByIdResponse;
    }

    //Метод определяет, надо ли увеличивать счётчик просмотров поста
    private boolean isIncrementViewCount(User user) {

        boolean isIncrementViewCount = true;

        User currentUser = getUserFromContext();

        if (currentUser.isModerator() || (currentUser.getId() == user.getId())) {
            isIncrementViewCount = false;
        }

        return isIncrementViewCount;
    }

    //Получение юзера из контекста
    private User getUserFromContext() throws UsernameNotFoundException {
        String email = SecurityUser.getEmailFromContext();

        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user " + email +
                " not found"));
    }

    //Проверки поста перед сохранением
    private boolean isCorrectPost(PostRequest postRequest,
                                  PostReturnResponse postPutResponse) {
        boolean isCorrectPost = true;

        if (postRequest.getTitle().length() < 3 || postRequest.getText().length() < 50) {

            postPutResponse.setResult(false);

            String title = null;
            String text = null;

            if (postRequest.getTitle().length() < 3) {
                title = "Заголовок не установлен";
            }
            if (postRequest.getText().length() < 50) {
                text = "Текст публикации слишком короткий";
            }

            postPutResponse.setErrors(new ErrorMessageResponse(title, text));

            isCorrectPost = false;
        }
        return isCorrectPost;
    }

    //Получаем правильную дату для поста из таймстампа
    private Date getRightDateFromTimeStamp(Long postTimestamp) {

        Timestamp timestamp = new Timestamp(postTimestamp);

        //Если указанное время меньше текущего, зададим текущее
        if (postTimestamp < System.currentTimeMillis()) {
            timestamp = new Timestamp(System.currentTimeMillis());
        }

        Date date = new Date(timestamp.getTime());

        return date;
    }

    //Добавляем пост
    public ResponseEntity<PostReturnResponse> addPost(PostRequest postRequest) {

        PostReturnResponse postPutResponse = new PostReturnResponse();

        //Проверяем пост
        if (isCorrectPost(postRequest, postPutResponse)) {
            //Ищем юзера в контексте
            User user = getUserFromContext();

            Post post = new Post();
            post.setActive(postRequest.getActive() == 1);
            post.setModerationStatus(ModerationStatus.NEW);
            post.setUser(user);
            post.setTime(getRightDateFromTimeStamp(postRequest.getTimestamp()));
            post.setTitle(postRequest.getTitle());
            post.setText(postRequest.getText());
            post.setViewCount(0l);

            Post newPost = postRepository.save(post);

            setTagsToPost(postRequest.getTags(), newPost.getId());

            postPutResponse.setResult(true);
        }


        return ResponseEntity.ok(postPutResponse);
    }

    //Сохраняем тэги для поста
    private void setTagsToPost(List<String> tags,
                               Long postId) {

        if (tags != null) {
            tags.forEach(t -> {

                Optional<Tag> tagOptional = tagRepository.findByName(t);

                Tag tag;

                if (tagOptional.isPresent()) {

                    tag = tagOptional.get();

                } else {

                    tag = new Tag();
                    tag.setName(t);
                    tag = tagRepository.save(tag);

                }

                Tag2Post tag2Post = new Tag2Post();
                tag2Post.setPostId(postId);
                tag2Post.setTagId(tag.getId());

                //Если нету связки поста с тэгом, то добавляем  её
                if (tag2PostRepository.countByTagIdAndPostId(tag.getId(), postId) == 0) {
                    tag2PostRepository.save(tag2Post);
                }

            });
        }
    }

    public ResponseEntity<PostReturnResponse> editPost(long postId, PostRequest postRequest) {
        PostReturnResponse postPutResponse = new PostReturnResponse();

        //Проверяем пост
        if (isCorrectPost(postRequest, postPutResponse)) {
            Post post = postRepository.findById(postId).get();

            User currentUser = getUserFromContext();

            //Если пост сохраняет автор и он не является модератором, присваиваем статус NEW
            if (post.getUser().getId() == currentUser.getId() && !currentUser.isModerator()) {
                post.setModerationStatus(ModerationStatus.NEW);
            }
            post.setTime(getRightDateFromTimeStamp(postRequest.getTimestamp()));
            post.setActive(postRequest.getActive() == 1);
            post.setTitle(postRequest.getTitle());
            post.setText(postRequest.getText());

            postRepository.save(post);

            setTagsToPost(postRequest.getTags(), postId);

            postPutResponse.setResult(true);
        }
        return ResponseEntity.ok(postPutResponse);
    }


}


