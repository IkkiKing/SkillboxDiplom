package com.ikkiking.service;

import com.ikkiking.api.request.PostRequest;
import com.ikkiking.api.request.VoteRequest;
import com.ikkiking.api.response.VoteResponse;
import com.ikkiking.api.response.post.CommentResponse;
import com.ikkiking.api.response.post.CommentUserResponse;
import com.ikkiking.api.response.post.PostByIdResponse;
import com.ikkiking.api.response.post.PostErrorResponse;
import com.ikkiking.api.response.post.PostForResponse;
import com.ikkiking.api.response.post.PostResponse;
import com.ikkiking.api.response.post.PostReturnResponse;
import com.ikkiking.api.response.post.UserResponse;
import com.ikkiking.base.ContextUser;
import com.ikkiking.base.DateHelper;
import com.ikkiking.base.exception.PostException;
import com.ikkiking.base.exception.PostNotFoundException;
import com.ikkiking.base.exception.VoteException;
import com.ikkiking.model.ModerationStatus;
import com.ikkiking.model.Post;
import com.ikkiking.model.PostVote;
import com.ikkiking.model.Tag;
import com.ikkiking.model.Tag2Post;
import com.ikkiking.model.User;
import com.ikkiking.repository.GlobalSettingsRepository;
import com.ikkiking.repository.PostRepository;
import com.ikkiking.repository.PostVoteRepository;
import com.ikkiking.repository.Tag2PostRepository;
import com.ikkiking.repository.TagRepository;
import com.ikkiking.repository.UserRepository;
import com.ikkiking.repository.Votes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostService {

    @Value("${post.title.length.min}")
    private int postTitleMinLength;

    @Value("${post.title.length.max}")
    private int postTitleMaxLength;

    @Value("${post.text.length.min}")
    private int postTextMinLength;

    @Value("${post.text.length.max}")
    private int postTextMaxLength;

    @Value("${post.announce.length.max}")
    private int postAnnounceMaxLength;


    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;
    private final TagRepository tagRepository;
    private final Tag2PostRepository tag2PostRepository;
    private final UserRepository userRepository;
    private final GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    public PostService(PostRepository postRepository,
                       PostVoteRepository postVoteRepository,
                       TagRepository tagRepository,
                       Tag2PostRepository tag2PostRepository,
                       UserRepository userRepository,
                       GlobalSettingsRepository globalSettingsRepository) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
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
    public ResponseEntity<PostResponse> posts(int limit,
                                              int offset,
                                              String mode) {

        Page<Post> postPage = getPostFromDb(postRepository, limit, offset, mode);
        PostResponse postResponse = convertToPostResponse(postPage);

        return ResponseEntity.ok(postResponse);
    }

    /**
     * Поиск постов.
     *
     * @param limit  кол-во выводимых постов
     * @param offset отступ от начала страницы
     * @param query  строка поиска(По названию/по тексту)
     */
    public ResponseEntity<PostResponse> searchPosts(int limit,
                                                    int offset,
                                                    String query) {

        Pageable sortedByMode = PageRequest.of(
                getPageByOffsetAndLimit(limit, offset),
                limit,
                Sort.by("time").descending());

        Page<Post> postPage;
        if (query.trim().isEmpty() || query == null) {
            postPage = postRepository.findAll(sortedByMode);
        } else {
            postPage = postRepository.findAllBySearch(sortedByMode, query);
        }

        PostResponse postResponse = convertToPostResponse(postPage);

        return ResponseEntity.ok(postResponse);
    }

    /**
     * Поиск постов за указанную дату.
     *
     * @param limit  кол-во выводимых постов
     * @param offset отступ от начала страницы
     * @param date   дата в формате 2018-02-12
     */
    public ResponseEntity<PostResponse> postsByDate(int limit,
                                                    int offset,
                                                    String date) {

        Pageable sortedByMode = PageRequest.of(
                getPageByOffsetAndLimit(limit, offset),
                limit,
                Sort.by("time").descending());

        Page<Post> postPage = postRepository.findAllByDate(sortedByMode, date);
        PostResponse postResponse = convertToPostResponse(postPage);

        return ResponseEntity.ok(postResponse);
    }

    /**
     * Поиск постов по тэгу.
     *
     * @param limit  кол-во выводимых постов
     * @param offset отступ от начала страницы
     * @param tag    тэг для поиска
     */
    public ResponseEntity<PostResponse> postsByTag(int limit,
                                                   int offset,
                                                   String tag) {
        Pageable sortedByMode = PageRequest.of(
                getPageByOffsetAndLimit(limit, offset),
                limit,
                Sort.by("time").descending());

        Page<Post> postPage = postRepository.findAllByTag(sortedByMode, tag);

        PostResponse postResponse = convertToPostResponse(postPage);

        return ResponseEntity.ok(postResponse);
    }

    /**
     * Поиск постов для модерации.
     *
     * @param limit  кол-во выводимых постов
     * @param offset отступ от начала страницы
     * @param status статус модерации
     */
    public ResponseEntity<PostResponse> postsForModeration(int limit,
                                                           int offset,
                                                           String status) {
        Pageable sortedByMode = PageRequest.of(
                getPageByOffsetAndLimit(limit, offset),
                limit,
                Sort.by("time").descending());

        String email = ContextUser.getEmailFromContext();

        Page<Post> postPage = status.equalsIgnoreCase(ModerationStatus.NEW.toString())
                ? postRepository.findAllForModeration(sortedByMode, status) :
                postRepository.findAllMyModeration(sortedByMode, email, status);

        PostResponse postResponse = convertToPostResponse(postPage);

        return ResponseEntity.ok(postResponse);
    }

    /**
     * Получение списка постов текущего пользователя.
     *
     * @param limit  кол-во выводимых постов
     * @param offset отступ от начала страницы
     * @param status статус поста
     */
    public ResponseEntity<PostResponse> myPosts(int limit,
                                                int offset,
                                                String status) {
        Pageable sortedByMode = PageRequest.of(
                getPageByOffsetAndLimit(limit, offset),
                limit,
                Sort.by("time").descending());
        String email = ContextUser.getEmailFromContext();

        int isActive = 1;
        String moderationStatus = null;
        switch (status) {
            case "inactive":
                isActive = 0;
                break;
            case "pending":
                moderationStatus = "NEW";
                break;
            case "declined":
                moderationStatus = "DECLINED";
                break;
            case "published":
                moderationStatus = "ACCEPTED";
                break;
            default:
                log.warn("UNKNOWN STATUS FROM FRONT");
        }
        Page<Post> postPage = postRepository.findMyPosts(
                sortedByMode,
                email,
                isActive,
                moderationStatus);

        PostResponse postResponse = convertToPostResponse(postPage);
        return ResponseEntity.ok(postResponse);
    }

    /**
     * Вспомогательный метод расчёта страницы.
     */
    private int getPageByOffsetAndLimit(int limit,
                                        int offset) {
        return offset / limit;
    }

    /**
     * Вспомогательный метод получения постов из БД.
     */
    private Page<Post> getPostFromDb(PostRepository postRepository,
                                     int limit,
                                     int offset,
                                     String mode) {

        Pageable pageable;
        Page<Post> posts;

        int page = getPageByOffsetAndLimit(limit, offset);

        log.info("Posts request by mode: " + mode + ". Offset is " + offset + ". Limit is " + limit);

        switch (mode) {
            case "early":
                pageable = PageRequest.of(page, limit, Sort.by("time").ascending());
                posts = postRepository.findAll(pageable);
                break;
            case "popular":
                pageable = PageRequest.of(page, limit);
                posts = postRepository.findAllByPopular(pageable);
                break;
            case "best":
                pageable = PageRequest.of(page, limit);
                posts = postRepository.findAllByBest(pageable);
                break;
            //RECENT
            default:
                pageable = PageRequest.of(page, limit, Sort.by("time").descending());
                posts = postRepository.findAll(pageable);
                break;
        }
        return posts;
    }

    /**
     * Вспомогательная процедура наполнения поста.
     */
    private PostResponse convertToPostResponse(Page<Post> postPage) {
        List<PostForResponse> listPosts = new ArrayList<>();
        postPage.get().forEach(post -> {

            Votes votes = postVoteRepository.getVotes(post.getId());

            UserResponse userResponse = new UserResponse(
                    post.getUser().getId(),
                    post.getUser().getName());

            String announce = post.getText()
                    .replaceAll("\\<.*?>", "")
                    .replaceAll("&nbsp;", " ");
            if (announce.length() > postAnnounceMaxLength) {
                announce = announce.substring(0, postAnnounceMaxLength).concat("...");
            }

            listPosts.add(new PostForResponse(post.getId(),
                    TimeUnit.MILLISECONDS.toSeconds(post.getTime().getTime()),
                    userResponse,
                    post.getTitle(),
                    announce,
                    votes.getLikes(),
                    votes.getDislikes(),
                    Long.valueOf(post.getCommentsList().size()),
                    post.getViewCount())
            );
        });
        PostResponse postResponse = new PostResponse();
        postResponse.setCount(postPage.getTotalElements());
        postResponse.setPosts(listPosts);
        return postResponse;
    }

    /**
     * Получение поста по ID.
     *
     * @param id Идентификатор поста
     */
    public ResponseEntity<PostByIdResponse> postById(long id) {

        Post post = validatePostById(id);

        UserResponse userResponse = new UserResponse(
                post.getUser().getId(),
                post.getUser().getName());

        Votes votes = postVoteRepository.getVotes(post.getId());
        List<CommentResponse> commentList = getCommentResponseList(post);

        Set<String> tagStrList = post.getTags().stream()
                .map(a -> a.getName())
                .collect(Collectors.toSet());

        PostByIdResponse postByIdResponse = new PostByIdResponse(
                post.getId(),
                TimeUnit.MILLISECONDS.toSeconds(post.getTime().getTime()),
                post.isActive(),
                userResponse,
                post.getTitle(),
                post.getText(),
                votes.getLikes(),
                votes.getDislikes(),
                post.getViewCount(),
                commentList,
                tagStrList);
        //Увеличиваем кол-во просмотров на 1
        if (isIncrementViewCount(post.getUser())) {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }
        return ResponseEntity.ok(postByIdResponse);
    }

    /**
     * Метод валидации поста по ID.
     */
    private Post validatePostById(long postId) {
        //Проверяем наличие поста в БД
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostNotFoundException("Пост не найден!"));

        //Если посте неактивен/Не утвержден/Его дата еще не наступила
        if (!post.isActive()
                || !post.getModerationStatus().equals(ModerationStatus.ACCEPTED)
                || post.getTime().compareTo(DateHelper.getCurrentDate().getTime()) == 1) {
            //А так же авторизованный пользователь не является его автором/модератором
            User user = ContextUser.getUser(userRepository).orElseThrow(() ->
                    new PostNotFoundException("Пользователь неавторизован. Пост недоступен"));
            if (!post.getUser().equals(user) && !user.isModerator()) {
                new PostNotFoundException("Пост недоступен!");
            }
        }
        return post;
    }

    /**
     * Вспомогательный метод заполняет коллекцию DTO CommentResponse.
     */
    private List<CommentResponse> getCommentResponseList(Post post) {
        return post.getCommentsList().stream()
                .map(p -> new CommentResponse(
                        p.getId(),
                        TimeUnit.MILLISECONDS.toSeconds(p.getTime().getTime()),
                        p.getText(),
                        new CommentUserResponse(
                                p.getUser().getId(),
                                p.getUser().getName(),
                                p.getUser().getPhoto())))
                .collect(Collectors.toList());
    }


    /**
     * Вспомогательный метод определяет, надо ли увеличивать счётчик просмотров поста.
     */
    private boolean isIncrementViewCount(User user) {

        boolean isIncrementViewCount = true;
        Optional<User> currentUser = ContextUser.getUser(userRepository);
        if (currentUser.isPresent()) {
            if (currentUser.get().isModerator()
                    || (currentUser.get().getId() == user.getId())) {
                isIncrementViewCount = false;
            }
        }
        return isIncrementViewCount;
    }

    /**
     * Добавление поста.
     */
    @Transactional
    public ResponseEntity<PostReturnResponse> addPost(PostRequest postRequest) {
        //Проверяем пост
        validatePost(postRequest);
        //Ищем юзера в контексте
        User user = ContextUser.getUserFromContext(userRepository);

        Post post = new Post();
        post.setActive(postRequest.getActive() == 1);
        post.setModerationStatus(ModerationStatus.NEW);

        //Если юзер является модератором или предмодерация отключена, пост сразу становится принят
        if (!SettingsService.getSettingsValue(globalSettingsRepository, "POST_PREMODERATION")
                || user.isModerator()) {
            post.setModerationStatus(ModerationStatus.ACCEPTED);
        } else {
            post.setModerationStatus(ModerationStatus.NEW);
        }
        post.setUser(user);
        post.setTime(DateHelper.getRightDateFromTimeStamp(postRequest.getTimestamp()));
        post.setTitle(postRequest.getTitle());
        post.setText(postRequest.getText());
        post.setViewCount(0L);

        Post newPost = postRepository.save(post);
        setTagsToPost(postRequest.getTags(), newPost.getId());

        return ResponseEntity.ok(new PostReturnResponse(true));
    }

    /**
     * Метод редактирования поста.
     */
    @Transactional
    public ResponseEntity<PostReturnResponse> editPost(long postId, PostRequest postRequest) {
        //Проверяем пост
        validatePost(postRequest);
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
        return ResponseEntity.ok(new PostReturnResponse(true));
    }

    /**
     * Вспомогательный метод, проверяет корректность поста перед сохранением.
     */
    private void validatePost(PostRequest postRequest) {
        PostErrorResponse postErrorResponse = new PostErrorResponse();
        if (postRequest.getTitle().length() < postTitleMinLength) {
            postErrorResponse.setTitle("Заголовок не установлен. Минимальная длина заголовка: "
                    + postTitleMinLength);
        }
        if (postRequest.getTitle().length() > postTitleMaxLength) {
            postErrorResponse.setTitle("Максимальная длина заголовка: "
                    + postTitleMaxLength);
        }
        if (postRequest.getText().length() < postTextMinLength) {
            postErrorResponse.setText("Текст публикации слишком короткий. Минимальный: "
                    + postTextMinLength);
        }
        if (postRequest.getText().length() > postTextMaxLength) {
            postErrorResponse.setText("Текст публикации слишком большой. Максимальный: "
                    + postTextMaxLength);
        }
        if (postErrorResponse.getTitle() != null
                || postErrorResponse.getText() != null) {
            throw new PostException(postErrorResponse);
        }
        log.info("Кол-во символов: " + postRequest.getText().length());
    }

    /**
     * Вспомогательный метод, сохранение тэгов к посту.
     */
    private void setTagsToPost(List<String> tags,
                               Long postId) {
        if (tags != null) {
            //Список добавляемых к посту тэгов
            List<Tag> tagList = tagRepository.findAllByNameIn(tags);
            tag2PostRepository.deleteAllByPostId(postId);

            List<Tag2Post> tag2PostList = tagList.stream().map(m -> {
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
     * Проставление лайка.
     */
    @Transactional
    public ResponseEntity<VoteResponse> like(VoteRequest voteRequest) {
        return ResponseEntity.ok(getVoteResponse(voteRequest, 1));
    }

    /**
     * Проставление дизлайка.
     */
    @Transactional
    public ResponseEntity<VoteResponse> dislike(VoteRequest voteRequest) {
        return ResponseEntity.ok(getVoteResponse(voteRequest, -1));
    }

    private VoteResponse getVoteResponse(VoteRequest voteRequest,
                                         Integer value) {
        Post post = postRepository.findById(voteRequest.getPostId()).orElseThrow(() ->
                new VoteException("Post not found in DB"));

        User user = ContextUser.getUserFromContext(userRepository);
        Optional<PostVote> postVoteOptional = postVoteRepository.findByPostIdAndUserId(
                voteRequest.getPostId(),
                user.getId());

        PostVote postVote = new PostVote();
        //Если запись уже есть в БД
        if (postVoteOptional.isPresent()) {
            postVote = postVoteOptional.get();
            if (postVoteOptional.get().getValue().equals(value)) {
                throw new VoteException("Vote already setted");
            }
        } else {
            postVote.setUser(user);
            postVote.setPost(post);
        }
        postVote.setTime(DateHelper.getCurrentDate().getTime());
        postVote.setValue(value);
        postVoteRepository.save(postVote);
        return new VoteResponse(true);
    }
}


