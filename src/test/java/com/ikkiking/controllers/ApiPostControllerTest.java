package com.ikkiking.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikkiking.api.request.PostRequest;
import com.ikkiking.api.request.VoteRequest;
import com.ikkiking.api.response.VoteResponse;
import com.ikkiking.api.response.post.PostByIdResponse;
import com.ikkiking.api.response.post.PostResponse;
import com.ikkiking.api.response.post.PostReturnResponse;
import com.ikkiking.base.ContextUser;
import com.ikkiking.base.DateHelper;
import com.ikkiking.model.ModerationStatus;
import com.ikkiking.model.Post;
import com.ikkiking.model.PostComments;
import com.ikkiking.model.PostVote;
import com.ikkiking.model.User;
import com.ikkiking.repository.PostCommentsRepository;
import com.ikkiking.repository.PostRepository;
import com.ikkiking.repository.PostVoteRepository;
import com.ikkiking.repository.TagRepository;
import com.ikkiking.repository.UserRepository;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import utils.TestUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class ApiPostControllerTest {

    private final static String TEST_TEXT = "TEST_TEXT_TEST_TEXT TEST_TEXT_TEST_TEXT"
            + "TEST_TEXT_TEST_TEXT TEST_TEXT_TEST_TEXT"
            + "TEST_TEXT_TEST_TEXT TEST_TEXT_TEST_TEXT"
            + "TEST_TEXT_TEST_TEXT TEST_TEXT_TEST_TEXT";
    private final static String TEST_TITLE = "TEST TITLE _ TEST";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostCommentsRepository postCommentsRepository;
    @Autowired
    private PostVoteRepository postVoteRepository;

    private TestUtil testUtil;

    @Before
    public void setUp() throws Exception {
        testUtil = new TestUtil(mockMvc, objectMapper);
    }

    /**
     * Проверка метода добавления поста.
     */
    @Test
    @WithUserDetails("VasilievVasiliy@gmail.com")
    public void step01_addPost() throws Exception {

        PostRequest postRequest = new PostRequest();
        postRequest.setActive(1);
        postRequest.setText("SHORT TEXT");
        postRequest.setTitle(TEST_TITLE);
        Calendar calendar = DateHelper.getCurrentDate();
        postRequest.setTimestamp(calendar.getTimeInMillis());
        List<String> tagList = tagRepository.findAllByNameIn(List.of("TEST_TAG_TEST"))
                .stream().map(tag -> tag.getName()).collect(Collectors.toList());
        postRequest.setTags(tagList);

        String result = testUtil.sendPost("/api/post", status().isOk(), postRequest);

        PostReturnResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.isResult()).isFalse();
        assertThat(response.getErrors().getText()).isNotNull();

        postRequest.setText(TEST_TEXT);
        result = testUtil.sendPost("/api/post", status().isOk(), postRequest);

        response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.isResult()).isTrue();
        assertThat(response.getErrors()).isNull();
    }

    /**
     * Проверка метода изменения поста.
     */
    @Test
    @WithUserDetails("VasilievVasiliy@gmail.com")
    public void step02_editPost() throws Exception {
        PostRequest postRequest = new PostRequest();
        postRequest.setActive(1);
        postRequest.setText(TEST_TEXT);
        postRequest.setTitle("TT");
        Calendar calendar = DateHelper.getCurrentDate();
        postRequest.setTimestamp(calendar.getTimeInMillis());

        Post post = postRepository.findTop();

        //EDIT
        String result = testUtil.sendPut("/api/post/" + post.getId(), status().isOk(), postRequest);
        PostReturnResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.isResult()).isFalse();
        assertThat(response.getErrors().getTitle()).isNotNull();

        postRequest.setTitle(TEST_TITLE);
        result = testUtil.sendPut("/api/post/" + post.getId(), status().isOk(), postRequest);

        response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.isResult()).isTrue();
        assertThat(response.getErrors()).isNull();
    }

    /**
     * Проверка метода получения последних постов.
     */
    @Test
    public void step03_getRecentPosts() throws Exception {
        Post post = postRepository.findTop();
        User user = userRepository.findTopByOrderByIdAsc();
        post.setModerationStatus(ModerationStatus.ACCEPTED);
        post.setModerator(user);
        postRepository.save(post);

        String result = testUtil.sendGet(
                "/api/post?offset=0&limit=10&mode=recent",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getCount() > 0).isTrue();
        assertThat(response.getPosts().isEmpty()).isFalse();
        assertThat(response.getPosts().get(0).getId()).isEqualTo(post.getId());
    }

    /**
     * Проверка метода получения ранних постов.
     */
    @Test
    public void step04_getEarlyPosts() throws Exception {
        //Меняем дату тестового поста на самую ранюю
        Calendar calendar = DateHelper.getCurrentDate();
        calendar.set(Calendar.YEAR, -10);

        Post post = postRepository.findTop();
        post.setTime(calendar.getTime());
        postRepository.save(post);

        String result = testUtil.sendGet(
                "/api/post?offset=0&limit=10&mode=early",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getCount() > 0).isTrue();
        assertThat(response.getPosts().isEmpty()).isFalse();
        assertThat(response.getPosts().get(0).getId()).isEqualTo(post.getId());
    }

    /**
     * Проверка метода получения популярных постов.
     */
    @Test
    public void step05_getPopularPosts() throws Exception {
        Post post = postRepository.findTop();
        User user = userRepository.findTopByOrderByIdAsc();

        List<PostComments> postCommentsList = new ArrayList<>();
        final int commentsCount = 20;
        for (int i = 0; i < commentsCount; i++) {
            PostComments postComments = new PostComments();
            postComments.setUser(user);
            postComments.setTime(DateHelper.getCurrentDate().getTime());
            postComments.setText("TEST_COMMENT" + i);
            postComments.setPost(post);
            postCommentsList.add(postComments);
        }
        postCommentsRepository.saveAll(postCommentsList);

        String result = testUtil.sendGet(
                "/api/post?offset=0&limit=10&mode=popular",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getCount() > 0).isTrue();
        assertThat(response.getPosts().isEmpty()).isFalse();
        assertThat(response.getPosts().get(0).getId()).isEqualTo(post.getId());
        assertThat(response.getPosts().get(0).getCommentCount()).isEqualTo(commentsCount);
    }

    /**
     * Проверка метода получения лучших постов.
     */
    @Test
    public void step06_getBestPosts() throws Exception {
        Post post = postRepository.findTop();
        User user = userRepository.findTopByOrderByIdAsc();

        List<PostVote> postVoteList = new ArrayList<>();
        final int likeCount = 20;
        for (int i = 0; i < likeCount; i++) {
            PostVote postVote = new PostVote();
            postVote.setUser(user);
            postVote.setTime(DateHelper.getCurrentDate().getTime());
            postVote.setValue(1);
            postVote.setPost(post);
            postVoteList.add(postVote);
        }
        postVoteRepository.saveAll(postVoteList);

        String result = testUtil.sendGet(
                "/api/post?offset=0&limit=10&mode=best",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });

        assertThat(response.getCount() > 0).isTrue();
        assertThat(response.getPosts().isEmpty()).isFalse();
        assertThat(response.getPosts().get(0).getId()).isEqualTo(post.getId());
        assertThat(response.getPosts().get(0).getLikeCount()).isEqualTo(likeCount);
    }

    /**
     * Проверка метода поиска постов.
     */
    @Test
    public void step07_search() throws Exception {
        Post post = postRepository.findTop();
        post.setText("TEXT_TO_FIND");
        postRepository.save(post);

        //postRepository.save()
        String result = testUtil.sendGet(
                "/api/post/search?offset=0&limit=10&query=" + post.getText(),
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getCount() > 0).isTrue();
        assertThat(response.getPosts().size() == 1);
        assertThat(response.getPosts().get(0).getId()).isEqualTo(post.getId());

        post.setTitle("TITLE_TO_FIND");
        postRepository.save(post);

        result = testUtil.sendGet(
                "/api/post/search?offset=0&limit=10&query=" + post.getTitle(),
                status().isOk());
        response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getCount() > 0).isTrue();
        assertThat(response.getPosts().size() == 1);
        assertThat(response.getPosts().get(0).getId()).isEqualTo(post.getId());
        assertThat(response.getPosts().get(0).getTitle()).isEqualTo(post.getTitle());
    }

    /**
     * Проверка метода поиска постов по дате.
     */
    @Test
    public void step08_postsByDate() throws Exception {
        //Меняем дату тестового поста на самую ранюю
        Post post = postRepository.findTopByOrderByIdDesc();
        Calendar calendar = DateHelper.getCurrentDate();
        calendar.set(2020, 0, 01);
        post.setTime(calendar.getTime());
        postRepository.save(post);

        String result = testUtil.sendGet(
                "/api/post/byDate?offset=0&limit=10&date=2020-01-01",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getCount() > 0).isTrue();
        assertThat(response.getPosts().size() == 1);
        assertThat(response.getPosts().get(0).getId().equals(post.getId()));
    }

    /**
     * Проверка метода поиска постов по тэгу.
     */
    @Test
    public void step09_postsByTag() throws Exception {
        String result = testUtil.sendGet(
                "/api/post/byTag?offset=0&limit=10&tag=Управление",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getCount() > 0).isTrue();
        assertThat(response.getPosts().isEmpty()).isFalse();
    }

    /**
     * Проверка метода постов для модерации.
     */
    @Test
    @WithUserDetails("VasilievVasiliy@gmail.com")
    public void step10_postsForModeration() throws Exception {
        Post post = postRepository.findTop();
        post.setModerationStatus(ModerationStatus.NEW);
        postRepository.save(post);

        String result = testUtil.sendGet(
                "/api/post/moderation?offset=0&limit=10&status=new",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getCount() > 0).isTrue();
        assertThat(response.getPosts().isEmpty()).isFalse();
        assertThat(response.getPosts()
                .stream().anyMatch(f -> f.getId().equals(post.getId()))).isTrue();


        post.setModerationStatus(ModerationStatus.ACCEPTED);
        postRepository.save(post);

        result = testUtil.sendGet(
                "/api/post/moderation?offset=0&limit=10&status=accepted",
                status().isOk());
        response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getCount() > 0).isTrue();
        assertThat(response.getPosts().isEmpty()).isFalse();
        assertThat(response.getPosts()
                .stream().anyMatch(f -> f.getId().equals(post.getId()))).isTrue();
    }

    /**
     * Проверка метода получения своих постов.
     */
    @Test
    @WithUserDetails("VasilievVasiliy@gmail.com")
    public void step11_getMyPosts() throws Exception {
        Post post = postRepository.findTop();
        post.setUser(ContextUser.getUser(userRepository).get());
        post.setModerationStatus(ModerationStatus.DECLINED);
        postRepository.save(post);

        String result = testUtil.sendGet(
                "/api/post/my?offset=0&limit=10&status=declined",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getCount() > 0).isTrue();
        assertThat(response.getPosts().isEmpty()).isFalse();

        post.setModerationStatus(ModerationStatus.ACCEPTED);
        postRepository.save(post);

        result = testUtil.sendGet(
                "/api/post/my?offset=0&limit=10&status=published",
                status().isOk());
        response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getCount() > 0).isTrue();
        assertThat(response.getPosts().isEmpty()).isFalse();
    }

    /**
     * Проверка метода поиска поста по ID.
     */
    @Test
    @WithUserDetails("VasilievVasiliy@gmail.com")
    public void step12_getPostById() throws Exception {
        Post post = postRepository.findTopByOrderByIdDesc();
        String result = testUtil.sendGet(
                "/api/post/" + post.getId(),
                status().isOk());
        PostByIdResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getId()).isEqualTo(post.getId());
    }


    /**
     * Проверка метода лайк.
     */
    @Test
    @WithUserDetails("VasilievVasiliy@gmail.com")
    public void step12_like() throws Exception {
        Post post = postRepository.findTop();
        VoteRequest voteRequest = new VoteRequest();
        voteRequest.setPostId(post.getId());

        String result = testUtil.sendPost("/api/post/like",
                status().isOk(), voteRequest);

        VoteResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.isResult()).isTrue();


        result = testUtil.sendPost("/api/post/like",
                status().isOk(), voteRequest);
        response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.isResult()).isFalse();

    }

    /**
     * Проверка метода дизлайк.
     */
    @Test
    @WithUserDetails("VasilievVasiliy@gmail.com")
    public void step14_dislike() throws Exception {
        Post post = postRepository.findTop();
        VoteRequest voteRequest = new VoteRequest();
        voteRequest.setPostId(post.getId());

        String result = testUtil.sendPost("/api/post/dislike",
                status().isOk(), voteRequest);

        VoteResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.isResult()).isTrue();


        result = testUtil.sendPost("/api/post/dislike",
                status().isOk(), voteRequest);
        response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.isResult()).isFalse();
    }
}
