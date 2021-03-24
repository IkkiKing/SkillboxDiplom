package com.ikkiking.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikkiking.api.response.post.PostByIdResponse;
import com.ikkiking.api.response.post.PostResponse;
import com.ikkiking.base.DateHelper;
import com.ikkiking.model.ModerationStatus;
import com.ikkiking.model.Post;
import com.ikkiking.model.PostComments;
import com.ikkiking.model.Tag;
import com.ikkiking.model.User;
import com.ikkiking.repository.PostRepository;
import com.ikkiking.repository.Tag2PostRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiPostControllerTest {

    private final static String EMAIL = "test-mail@yandex.ru";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private Tag2PostRepository tag2PostRepository;
    @Autowired
    private UserRepository userRepository;

    private Post post;

    private TestUtil testUtil;

    @Before
    @Transactional
    public void setUp() throws Exception {
        testUtil = new TestUtil(mockMvc, objectMapper);

        User user = userRepository.findById(1L).get();

        Tag tag = new Tag();
        tag.setName("TEST_TAG_TEST");

        post = new Post();
        post.setActive(true);
        post.setModerationStatus(ModerationStatus.ACCEPTED);
        post.setTitle("TEST_TITLE_TEST");
        post.setText("TEST_TEXT_TEST_TEXT TEST_TEXT_TEST_TEXT"
                + "TEST_TEXT_TEST_TEXT TEST_TEXT_TEST_TEXT"
                + "TEST_TEXT_TEST_TEXT TEST_TEXT_TEST_TEXT"
                + "TEST_TEXT_TEST_TEXT TEST_TEXT_TEST_TEXT");

        post.setUser(user);
        post.setViewCount(100L);
        post.setTime(DateHelper.getCurrentDate().getTime());
        post.setModerator(user);
        post.setTags(List.of(tag));

        List<PostComments> postCommentsList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            PostComments postComments = new PostComments();
            postComments.setUser(user);
            postComments.setTime(DateHelper.getCurrentDate().getTime());
            postComments.setText("TEST_COMMENT" + i);
            postComments.setPost(post);
            postCommentsList.add(postComments);
        }
        post.setCommentsList(postCommentsList);
        postRepository.save(post);

        //Tag already saved by setter in post?
        /*tagRepository.save(tag);
        Tag2Post tag2Post = new Tag2Post();
        tag2Post.setTagId(tag.getId());
        tag2Post.setPostId(post.getId());
        tag2PostRepository.save(tag2Post);

        userRepository.save(user);*/
    }

    /**
     * Проверка метода получения последних постов.
     */
    @Test
    public void step01_getRecentPosts() throws Exception {
        String result = testUtil.sendGet(
                "/api/post?offset=0&limit=10&mode=recent",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat((response.getCount() > 0)).isTrue();
        assertThat((response.getPosts().isEmpty())).isFalse();
        assertThat((response.getPosts().get(0).getId().equals(post.getId())));
    }

    /**
     * Проверка метода получения ранних постов.
     */
    @Test
    public void step02_getEarlyPosts() throws Exception {
        //Меняем дату тестового поста на самую ранюю
        Calendar calendar = DateHelper.getCurrentDate();
        calendar.set(Calendar.YEAR, -10);
        post.setTime(calendar.getTime());
        postRepository.save(post);

        String result = testUtil.sendGet(
                "/api/post?offset=0&limit=10&mode=early",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat((response.getCount() > 0)).isTrue();
        assertThat((response.getPosts().isEmpty())).isFalse();
        assertThat((response.getPosts().get(0).getId().equals(post.getId())));
    }

    /**
     * Проверка метода получения популярных постов.
     */
    @Test
    public void step03_getPopularPosts() throws Exception {
        String result = testUtil.sendGet(
                "/api/post?offset=0&limit=10&mode=popular",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat((response.getCount() > 0)).isTrue();
        assertThat((response.getPosts().isEmpty())).isFalse();
        assertThat((response.getPosts().get(0).getId().equals(post.getId())));
    }

    /**
     * Проверка метода получения лучших постов.
     */
    @Test
    public void step04_getBestPosts() throws Exception {
        String result = testUtil.sendGet(
                "/api/post?offset=0&limit=10&mode=best",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat((response.getCount() > 0)).isTrue();
        assertThat((response.getPosts().isEmpty())).isFalse();
        assertThat((response.getPosts().get(0).getId().equals(post.getId())));
    }

    /**
     * Проверка метода поиска постов.
     */
    @Test
    public void step04_search() throws Exception {
        String result = testUtil.sendGet(
                "/api/post/search?offset=0&limit=10&query=TEST_TITLE",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat((response.getCount() > 0)).isTrue();
        assertThat((response.getPosts().size() == 1));
        assertThat((response.getPosts().get(0).getId().equals(post.getId())));

        result = testUtil.sendGet(
                "/api/post/search?offset=0&limit=10&query=TEST_TEXT_TEST_TEXT",
                status().isOk());
        response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat((response.getCount() > 0)).isTrue();
        assertThat((response.getPosts().size() == 1));
        assertThat((response.getPosts().get(0).getId().equals(post.getId())));
    }

    /**
     * Проверка метода поиска постов по дате.
     */
    @Test
    public void step05_postsByDate() throws Exception {
        //Меняем дату тестового поста на самую ранюю
        Calendar calendar = DateHelper.getCurrentDate();
        calendar.set(2020, 0, 01);
        post.setTime(calendar.getTime());
        postRepository.save(post);

        String result = testUtil.sendGet(
                "/api/post/byDate?offset=0&limit=10&date=2020-01-01",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat((response.getCount() > 0)).isTrue();
        assertThat((response.getPosts().size() == 1));
        assertThat((response.getPosts().get(0).getId().equals(post.getId())));
    }

    /**
     * Проверка метода поиска постов по тэгу.
     */
    @Test
    public void step06_postsByTag() throws Exception {
        String result = testUtil.sendGet(
                "/api/post/byTag?offset=0&limit=10&tag=Управление",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat((response.getCount() > 0)).isTrue();
        assertThat((response.getPosts().isEmpty())).isFalse();
    }

    /**
     * Проверка метода поиска постов по тэгу.
     */
    @Test
    @WithUserDetails("VasilievVasiliy@gmail.com")
    public void step07_postsForModeration() throws Exception {
        post.setModerationStatus(ModerationStatus.NEW);
        postRepository.save(post);
        String result = testUtil.sendGet(
                "/api/post/moderation?offset=0&limit=10&status=new",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat((response.getCount() > 0)).isTrue();
        assertThat((response.getPosts().isEmpty())).isFalse();
        assertThat((response.getPosts()
                .stream().anyMatch(f -> f.getId().equals(post.getId())))).isTrue();

        post.setModerationStatus(ModerationStatus.ACCEPTED);
        postRepository.save(post);

        result = testUtil.sendGet(
                "/api/post/moderation?offset=0&limit=10&status=accepted",
                status().isOk());
        response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat((response.getCount() > 0)).isTrue();
        assertThat((response.getPosts().isEmpty())).isFalse();
        assertThat((response.getPosts()
                .stream().anyMatch(f -> f.getId().equals(post.getId())))).isTrue();
    }

    /**
     * Проверка метода поиска постов по тэгу.
     */
    @Test
    @WithUserDetails("VasilievVasiliy@gmail.com")
    public void step08_getMyPosts() throws Exception {
        post.setModerationStatus(ModerationStatus.DECLINED);
        postRepository.save(post);

        String result = testUtil.sendGet(
                "/api/post/my?offset=0&limit=10&status=declined",
                status().isOk());
        PostResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat((response.getCount() > 0)).isTrue();
        assertThat((response.getPosts().isEmpty())).isFalse();

        post.setModerationStatus(ModerationStatus.ACCEPTED);
        postRepository.save(post);

        result = testUtil.sendGet(
                "/api/post/my?offset=0&limit=10&status=published",
                status().isOk());
        response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat((response.getCount() > 0)).isTrue();
        assertThat((response.getPosts().isEmpty())).isFalse();
    }

    /**
     * Проверка метода поиска постов по тэгу.
     */
    @Test
    @WithUserDetails("VasilievVasiliy@gmail.com")
    public void step08_getPostById() throws Exception {
        String result = testUtil.sendGet(
                "/api/post/" + post.getId(),
                status().isOk());
        PostByIdResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat((response.getId())).isEqualTo(post.getId());
    }

    /**
     * Проверка метода поиска постов по тэгу.
     */
    /*@Test
    @WithUserDetails("VasilievVasiliy@gmail.com")
    public void step08_addPost() throws Exception {
        PostRequest postRequest = new PostRequest();
        postRequest.setActive(1);
        postRequest.setText("SHORT TEXT");
        postRequest.setTitle("TEST TITLE");
        postRequest.setTimestamp(DateHelper.getCurrentDate().getTimeInMillis());

        List<String> tagList = tagRepository.findAllByNameIn(List.of("TEST_TAG_TEST"))
                .stream().map(tag -> tag.getName()).collect(Collectors.toList());
        postRequest.setTags(tagList);


        String result = testUtil.sendPost("/api/post/" + post.getId(),
                status().isOk(), postRequest);

        PostByIdResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat((response.getId())).isEqualTo(post.getId());
    }*/
    /*ADD EDIT LIKE DISLIKE*/

}
