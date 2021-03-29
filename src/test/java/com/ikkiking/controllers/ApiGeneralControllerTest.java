package com.ikkiking.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikkiking.api.request.CommentRequest;
import com.ikkiking.api.request.ModerationRequest;
import com.ikkiking.api.request.SettingsRequest;
import com.ikkiking.api.response.CalendarResponse;
import com.ikkiking.api.response.CommentAddResponse;
import com.ikkiking.api.response.InitResponse;
import com.ikkiking.api.response.ModerationResponse;
import com.ikkiking.api.response.SettingsResponse;
import com.ikkiking.api.response.statistic.StatisticResponse;
import com.ikkiking.api.response.tag.TagResponse;
import com.ikkiking.model.Post;
import com.ikkiking.repository.PostRepository;
import com.ikkiking.repository.StatisticCustom;
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
import utils.TestUtil;
import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiGeneralControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostRepository postRepository;

    private TestUtil testUtil;

    @Before
    public void setUp() throws Exception {
        testUtil = new TestUtil(mockMvc, objectMapper);
    }

    /**
     * Проверка основной информации страницы.
     */
    @Test
    public void step01_init() throws Exception {
        String result = testUtil.sendGet("/api/init", status().isOk());
        InitResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getTitle()).isNotNull();
        assertThat(response.getSubtitle()).isNotNull();
        assertThat(response.getEmail()).isNotNull();
        assertThat(response.getPhone()).isNotNull();
        assertThat(response.getCopyright()).isNotNull();
        assertThat(response.getCopyrightFrom()).isNotNull();
    }

    /**
     * Проверка получения и изменения настроек.
     */
    @Test
    @WithUserDetails("VasilievVasiliy@gmail.com")
    public void step02_settings() throws Exception {
        SettingsRequest settingsRequest = new SettingsRequest();
        settingsRequest.setMultiUserMode(false);
        settingsRequest.setPostPreModeration(false);
        settingsRequest.setStatisticIsPublic(false);
        testUtil.sendPut("/api/settings", status().isOk(), settingsRequest);

        String result = testUtil.sendGet("/api/settings", status().isOk());
        SettingsResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.isMultiUserMode()).isFalse();
        assertThat(response.isPostPremoderation()).isFalse();
        assertThat(response.isStatisticsIsPublic()).isFalse();

        settingsRequest.setMultiUserMode(true);
        settingsRequest.setPostPreModeration(true);
        settingsRequest.setStatisticIsPublic(true);
        testUtil.sendPut("/api/settings", status().isOk(), settingsRequest);

        result = testUtil.sendGet("/api/settings", status().isOk());
        response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.isMultiUserMode()).isTrue();
        assertThat(response.isPostPremoderation()).isTrue();
        assertThat(response.isStatisticsIsPublic()).isTrue();
    }

    /**
     * Проверка тэгов.
     */
    @Test
    public void step03_tag() throws Exception {
        String result = testUtil.sendGet("/api/tag", status().isOk());
        TagResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getTags()).isNotEmpty();

        final String query = "Управление";
        result = testUtil.sendGet("/api/tag?query=" + query, status().isOk());
        response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getTags().size() == 1).isTrue();
    }

    /**
     * Проверка календаря.
     */
    @Test
    public void step04_calendar() throws Exception {
        String result = testUtil.sendGet("/api/calendar", status().isOk());
        CalendarResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getYears()).isNotEmpty();

        final int year = 2020;
        result = testUtil.sendGet("/api/calendar?year=" + year, status().isOk());
        response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.getYears()).isNotEmpty();
        assertThat(response.getPosts().keySet().stream()
                .filter(f -> f.contains(String.valueOf(year)))
                .findAny().isPresent()).isTrue();
    }

    /**
     * Проверка своей статистики.
     */
    @Test
    @WithUserDetails("VasilievVasiliy@gmail.com")
    public void step05_statisticsMy() throws Exception {
        String result = testUtil.sendGet("/api/statistics/my", status().isOk());
        StatisticResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        StatisticCustom statisticCustom = postRepository.findMyStatisticByUserId(1L);

        assertThat(response.getFirstPublication()).isEqualTo(
                TimeUnit.MILLISECONDS.toSeconds(statisticCustom.getFirstPublication().getTime()));
    }

    /**
     * Проверка общей статистики.
     */
    @Test
    public void step06_statisticsAll() throws Exception {
        String result = testUtil.sendGet("/api/statistics/all", status().isOk());
        StatisticResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        Post post = postRepository.findById(1L).get();

        assertThat(response.getFirstPublication()).isEqualTo(
                TimeUnit.MILLISECONDS.toSeconds(post.getTime().getTime()));
    }

    /**
     * Проверка добавления комментария.
     */
    @Test
    @WithUserDetails("VasilievVasiliy@gmail.com")
    public void step07_comment() throws Exception {
        CommentRequest commentRequest = new CommentRequest();
        final Long postId = 1L;
        commentRequest.setPostId(postId);
        commentRequest.setText("TE");

        String result = testUtil.sendPost("/api/comment", status().isBadRequest(), commentRequest);
        CommentAddResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.isResult()).isFalse();
        assertThat(response.getErrors().getText()).isNotNull();

        final String text = "TEST_TEXT";
        commentRequest.setPostId(postId);
        commentRequest.setText(text);
        result = testUtil.sendPost("/api/comment", status().isOk(), commentRequest);
        response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.isResult()).isTrue();
    }

    /**
     * Проверка модерации поста.
     */
    @Test
    @WithUserDetails("VasilievVasiliy@gmail.com")
    public void step08_moderate() throws Exception {
        ModerationRequest moderationRequest = new ModerationRequest();
        moderationRequest.setPostId(2L);
        moderationRequest.setDecision("decline");

        String result = testUtil.sendPost("/api/moderation", status().isOk(), moderationRequest);
        ModerationResponse response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.isResult()).isTrue();

        moderationRequest.setDecision("accept");

        result = testUtil.sendPost("/api/moderation", status().isOk(), moderationRequest);
        response = objectMapper.readValue(result, new TypeReference<>() {
        });
        assertThat(response.isResult()).isTrue();
    }

}
