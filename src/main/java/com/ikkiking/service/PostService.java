package com.ikkiking.service;


import com.ikkiking.api.request.PostRequest;
import com.ikkiking.api.request.VoteRequest;
import com.ikkiking.api.response.PostResponse.*;
import com.ikkiking.api.response.VoteResponse;
import com.ikkiking.base.ContextUser;
import com.ikkiking.base.DateHelper;
import com.ikkiking.base.exception.PostNotFoundException;
import com.ikkiking.model.*;
import com.ikkiking.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;
    private final PostCommentsRepository postCommentsRepository;
    private final TagRepository tagRepository;
    private final Tag2PostRepository tag2PostRepository;
    private final UserRepository userRepository;
    private final GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    public PostService(PostRepository postRepository,
                       PostVoteRepository postVoteRepository,
                       PostCommentsRepository postCommentsRepository,
                       TagRepository tagRepository,
                       Tag2PostRepository tag2PostRepository,
                       UserRepository userRepository,
                       GlobalSettingsRepository globalSettingsRepository) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
        this.postCommentsRepository = postCommentsRepository;
        this.tagRepository = tagRepository;
        this.tag2PostRepository = tag2PostRepository;
        this.userRepository = userRepository;
        this.globalSettingsRepository = globalSettingsRepository;
    }

    /**
     * Получение постов.
     *
     * @param limit  кол-во выводимых постов
     * @param offset отступ от начала страницы
     * @param mode   режим показа постов(Сортировка)
     */
    public ResponseEntity<GetPostResponse> posts(int limit,
                                                 int offset,
                                                 String mode) {

        Page<Post> postPage = getPostFromDb(postRepository, limit, offset, mode);

        GetPostResponse postResponse = new GetPostResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return ResponseEntity.ok(postResponse);
    }

    /**
     * Поиск постов.
     *
     * @param limit  кол-во выводимых постов
     * @param offset отступ от начала страницы
     * @param query  строка поиска(По названию/по тексту)
     */
    public ResponseEntity<SearchPostResponse> searchPosts(int limit,
                                                          int offset,
                                                          String query) {

        Pageable sortedByMode = PageRequest.of(
                getPageByOffsetAndLimit(limit, offset),
                limit,
                Sort.by("time").descending());

        Page<Post> postPage;
        if (query.isEmpty() || query == null) {
            postPage = postRepository.findAll(sortedByMode);
        } else {
            postPage = postRepository.findAllBySearch(sortedByMode, query);
        }

        SearchPostResponse postResponse = new SearchPostResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return ResponseEntity.ok(postResponse);
    }

    /**
     * Поиск постов за указанную дату.
     *
     * @param limit  кол-во выводимых постов
     * @param offset отступ от начала страницы
     * @param date   дата в формате 2018-02-12
     */
    public ResponseEntity<PostByDateResponse> postsByDate(int limit,
                                                          int offset,
                                                          String date) {

        Pageable sortedByMode = PageRequest.of(
                getPageByOffsetAndLimit(limit, offset),
                limit,
                Sort.by("time").descending());

        Page<Post> postPage = postRepository.findAllByDate(sortedByMode, date);

        PostByDateResponse postResponse = new PostByDateResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return ResponseEntity.ok(postResponse);
    }

    /**
     * Поиск постов по тэгу.
     *
     * @param limit  кол-во выводимых постов
     * @param offset отступ от начала страницы
     * @param tag    тэг для поиска
     */
    public ResponseEntity<PostByTagResponse> postsByTag(int limit,
                                                        int offset,
                                                        String tag) {
        Pageable sortedByMode = PageRequest.of(
                getPageByOffsetAndLimit(limit, offset),
                limit,
                Sort.by("time").descending());

        Page<Post> postPage = postRepository.findAllByTag(sortedByMode, tag);

        PostByTagResponse postResponse = new PostByTagResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return ResponseEntity.ok(postResponse);
    }

    /**
     * Поиск постов для модерации
     *
     * @param limit  кол-во выводимых постов
     * @param offset отступ от начала страницы
     * @param status статус модерации
     */
    public ResponseEntity<PostForModerationResponse> postsForModeration(int limit,
                                                                        int offset,
                                                                        String status) {
        Pageable sortedByMode = PageRequest.of(
                getPageByOffsetAndLimit(limit, offset),
                limit,
                Sort.by("time").descending()
        );

        String email = ContextUser.getEmailFromContext();

        Page<Post> postPage = status.equalsIgnoreCase(ModerationStatus.NEW.toString()) ?
                postRepository.findAllForModeration(sortedByMode, status) :
                postRepository.findAllMyModeration(sortedByMode, email, status);

        PostForModerationResponse postResponse = new PostForModerationResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return ResponseEntity.ok(postResponse);
    }

    /**
     * Получение списка постов текущего пользователя.
     *
     * @param limit  кол-во выводимых постов
     * @param offset отступ от начала страницы
     * @param status статус поста
     */
    public ResponseEntity<MyPostResponse> myPosts(int limit,
                                                  int offset,
                                                  String status) {
        Pageable sortedByMode = PageRequest.of(
                getPageByOffsetAndLimit(limit, offset),
                limit,
                Sort.by("time").descending()
        );

        String email = ContextUser.getEmailFromContext();

        int isActive = 1;
        String moderationStatus = null;

        switch (status) {
            case "inactive": {
                isActive = 0;
                break;
            }
            case "pending": {
                moderationStatus = "NEW";
                break;
            }
            case "declined": {
                moderationStatus = "DECLINED";
                break;
            }
            case "published": {
                moderationStatus = "ACCEPTED";
                break;
            }
        }

        Page<Post> postPage = postRepository.findMyPosts(
                sortedByMode,
                email,
                isActive,
                moderationStatus);

        MyPostResponse postResponse = new MyPostResponse();

        enrichPost(postVoteRepository, postCommentsRepository, postPage, postResponse);

        return ResponseEntity.ok(postResponse);
    }

    /**
     * Вспомогательный метод расчёта страницы.
     */
    private static int getPageByOffsetAndLimit(int limit,
                                               int offset) {
        return offset / limit;
    }

    /**
     * Вспомогательный метод получения постов из БД.
     */
    private static Page<Post> getPostFromDb(PostRepository postRepository,
                                            int limit,
                                            int offset,
                                            String mode) {

        Pageable sortedByMode;
        Page<Post> posts;

        int page = getPageByOffsetAndLimit(limit, offset);

        log.info("Posts request by mode: " + mode + ". Offset is " + offset + ". Limit is " + limit);

        switch (mode) {
            case "early": {
                sortedByMode = PageRequest.of(page, limit, Sort.by("time").ascending());
                posts = postRepository.findAll(sortedByMode);
                break;
            }
            case "popular": {
                sortedByMode = PageRequest.of(page, limit);
                posts = postRepository.findAllByPopular(sortedByMode);
                break;
            }
            case "best": {
                sortedByMode = PageRequest.of(page, limit);
                posts = postRepository.findAllByBest(sortedByMode);
                break;
            }
            //RECENT
            default: {
                sortedByMode = PageRequest.of(page, limit, Sort.by("time").descending());
                posts = postRepository.findAll(sortedByMode);
                break;
            }
        }
        return posts;
    }

    /**
     * Вспомогательная процедура наполнения поста.
     */
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

                long likesCount = postVoteRepository.countLikesByPostId(postId);
                long dislikesCount = postVoteRepository.countDislikesByPostId(postId);
                long commentCount = postCommentsRepository.countByPostId(postId);

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

    /**
     * Получение поста по ID.
     * @param id Идентификатор поста
     */
    public ResponseEntity<PostByIdResponse> postById(long id) {

        Post post = postRepository.findById(id).orElseThrow(
                () -> new PostNotFoundException("Пост не найден!"));

        Long postId = post.getId();
        long timestamp = post.getTime().getTime() / 1000L;
        boolean isActive = post.isActive();

        UserResponse user = new UserResponse(
                post.getUser().getId(),
                post.getUser().getName()
        );

        String title = post.getTitle();
        String text = post.getText();

        long likesCount = postVoteRepository.countLikesByPostId(postId);
        long dislikesCount = postVoteRepository.countDislikesByPostId(postId);
        long viewCount = post.getViewCount();

        List<PostComments> postCommentsList = postCommentsRepository.findAllByPostId(postId);

        List<CommentResponse> commentList = postCommentsList.stream()
                .map(p -> new CommentResponse(
                        p.getId(),
                        p.getTime().getTime() / 1_000L,
                        p.getText(),
                        new CommentUserResponse(
                                p.getUser().getId(),
                                p.getUser().getName(),
                                p.getUser().getPhoto())))
                .collect(Collectors.toList());

        List<Tag> tagList = tagRepository.findAllByPostId(postId);

        Set<String> tagStrList = tagList.stream()
                .map(a -> a.getName())
                .collect(Collectors.toSet());

        PostByIdResponse postByIdResponse = new PostByIdResponse(
                post.getId(),
                timestamp,
                isActive,
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

        return ResponseEntity.ok(postByIdResponse);
    }

    /**
     * Вспомогательный метод определяет, надо ли увеличивать счётчик просмотров поста.
     * */
    private boolean isIncrementViewCount(User user) {

        boolean isIncrementViewCount = true;

        try {
            User currentUser = ContextUser.getUserFromContext(userRepository);

            if (currentUser.isModerator() || (currentUser.getId() == user.getId())) {
                isIncrementViewCount = false;
            }
        } catch (UsernameNotFoundException ex) {
            log.info("User unauthorized found, increment views");
        }
        return isIncrementViewCount;
    }

    /**
     * Добавление поста.
     * */
    @Transactional
    public ResponseEntity<PostReturnResponse> addPost(PostRequest postRequest) {

        PostReturnResponse postPutResponse = new PostReturnResponse();

        //Проверяем пост
        if (isCorrectPost(postRequest, postPutResponse)) {
            //Ищем юзера в контексте
            User user = ContextUser.getUserFromContext(userRepository);

            Post post = new Post();
            post.setActive(postRequest.getActive() == 1);
            post.setModerationStatus(ModerationStatus.NEW);

            //Если юзер является модератором или предмодерация отключена, пост сразу становится принят
            if (!SettingsService.getSettingsValue(globalSettingsRepository, "POST_PREMODERATION") || user.isModerator()) {
                post.setModerationStatus(ModerationStatus.ACCEPTED);
            } else {
                post.setModerationStatus(ModerationStatus.NEW);
            }

            post.setUser(user);
            post.setTime(DateHelper.getRightDateFromTimeStamp(postRequest.getTimestamp()));
            post.setTitle(postRequest.getTitle());
            post.setText(postRequest.getText());
            post.setViewCount(0l);

            Post newPost = postRepository.save(post);

            setTagsToPost(postRequest.getTags(), newPost.getId());

            postPutResponse.setResult(true);
        }
        return ResponseEntity.ok(postPutResponse);
    }

    /**
     * Вспомогательный метод, проверяет корректность поста перед сохранением.
     * */
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

            postPutResponse.setErrors(new PostErrorResponse(title, text));

            isCorrectPost = false;

            log.warn("Post is not correct");
        }

        return isCorrectPost;
    }

    /**
     * Вспомогательный метод, сохранение тэгов к посту.
     * */
    private void setTagsToPost(List<String> tags,
                               Long postId) {

        if (tags != null) {

            //Список добавляемых к посту тэгов
            List<Tag> tagList = tagRepository.findAllByNameIn(tags);
            tag2PostRepository.deleteAllByPostId(postId);

            List<Tag2Post> tag2PostList = tagList.stream().map(m ->
                    {
                        Tag2Post tag2Post = new Tag2Post();
                        tag2Post.setTagId(m.getId());
                        tag2Post.setPostId(postId);
                        return tag2Post;
                    }
            ).collect(Collectors.toList());
            tag2PostRepository.saveAll(tag2PostList);
        }
    }

    /**
     * Метод редактирования поста.
     * */
    @Transactional
    public ResponseEntity<PostReturnResponse> editPost(long postId, PostRequest postRequest) {
        PostReturnResponse postPutResponse = new PostReturnResponse();

        //Проверяем пост
        if (isCorrectPost(postRequest, postPutResponse)) {
            Post post = postRepository.findById(postId).get();

            User currentUser = ContextUser.getUserFromContext(userRepository);

            //Если пост сохраняет автор и он не является модератором, присваиваем статус NEW
            if (post.getUser().getId() == currentUser.getId() && !currentUser.isModerator()) {
                post.setModerationStatus(ModerationStatus.NEW);
            }
            post.setTime(DateHelper.getRightDateFromTimeStamp(postRequest.getTimestamp()));
            post.setActive(postRequest.getActive() == 1);
            post.setTitle(postRequest.getTitle());
            post.setText(postRequest.getText());
            postRepository.save(post);

            setTagsToPost(postRequest.getTags(), postId);

            postPutResponse.setResult(true);
        }
        return ResponseEntity.ok(postPutResponse);
    }

    /**
     * Проставление лайка.
     * */
    @Transactional
    public ResponseEntity<VoteResponse> like(VoteRequest voteRequest) {
        return ResponseEntity.ok(getVoteResponse(voteRequest, 1));
    }

    /**
     * Проставление дизлайка.
     * */
    @Transactional
    public ResponseEntity<VoteResponse> dislike(VoteRequest voteRequest) {
        return ResponseEntity.ok(getVoteResponse(voteRequest, 0));
    }

    private VoteResponse getVoteResponse(VoteRequest voteRequest,
                                         Integer value) {

        VoteResponse voteResponse = new VoteResponse();

        Optional<Post> postOptional = postRepository.findById(voteRequest.getPostId());
        //Если пост не найден в БД, дальше делать нечего
        if (!postOptional.isPresent()) {
            log.warn("Post not found in DB");

            voteResponse.setResult(false);
            return voteResponse;
        }

        User user = ContextUser.getUserFromContext(userRepository);

        Optional<PostVote> postVoteOptional = postVoteRepository.findByPostIdAndUserId(voteRequest.getPostId(), user.getId());

        PostVote postVote;
        //Если запись уже есть в БД
        if (postVoteOptional.isPresent()) {
            postVote = postVoteOptional.get();

            //Если ранее был уже поставлен аналогичный Vote
            if (postVote.getValue() == value) {
                log.warn("Vote already setted");

                voteResponse.setResult(false);
            } else {
                postVote.setValue(value);
                postVoteRepository.save(postVote);
                voteResponse.setResult(true);
            }
        } else {
            postVote = new PostVote();
            postVote.setUser(user);
            postVote.setPost(postOptional.get());
            postVote.setTime(DateHelper.getCurrentDate().getTime());
            postVote.setValue(value);
            postVoteRepository.save(postVote);
            voteResponse.setResult(true);
        }
        return voteResponse;
    }
}


