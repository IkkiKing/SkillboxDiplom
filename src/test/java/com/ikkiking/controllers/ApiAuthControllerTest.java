package com.ikkiking.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikkiking.api.request.LoginRequest;
import com.ikkiking.api.request.PasswordRequest;
import com.ikkiking.api.request.RegisterRequest;
import com.ikkiking.api.request.RestoreRequest;
import com.ikkiking.api.response.LoginResponse;
import com.ikkiking.api.response.PasswordResponse;
import com.ikkiking.api.response.RegisterResponse;
import com.ikkiking.api.response.RestoreResponse;
import com.ikkiking.api.response.auth.AuthCaptchaResponse;
import com.ikkiking.api.response.auth.AuthLogoutResponse;
import com.ikkiking.model.CaptchaCodes;
import com.ikkiking.model.User;
import com.ikkiking.repository.CaptchaCodesRepository;
import com.ikkiking.repository.UserRepository;
import org.jeasy.random.EasyRandom;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiAuthControllerTest {

    private final static String EMAIL = "test-mail@yandex.ru";
    private final static String CODE = "12345678910";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CaptchaCodesRepository captchaCodesRepository;
    @Autowired
    private UserRepository userRepository;

    private TestUtil testUtil;

    @Before
    public void setUp(){
        testUtil = new TestUtil(mockMvc, objectMapper);
    }

    /**
     * Проверка сформированной капчи на наличие ключевых полей.
     * */
    @Test
    public void step01_captcha() throws Exception {
        String result = testUtil.sendGet("/api/auth/captcha", status().isOk());
        AuthCaptchaResponse response = objectMapper.readValue(result, new TypeReference<>() {});
        assertThat(response.getImage()).isNotNull();
        assertThat(response.getSecret()).isNotNull();
    }


    /**
     * Проверка регистрации 2 варианта.
     * 1. С правильным заполнением полей.
     * 2. С неправильным паролем и емейлом который уже существует.
     * */
    @Test
    public void step02_register() throws Exception {
        EasyRandom easyRandom = testUtil.getEasyRandom();
        CaptchaCodes captchaCodes = easyRandom.nextObject(CaptchaCodes.class);
        captchaCodesRepository.save(captchaCodes);

        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setEmail(EMAIL);
        registerRequest.setName("TEST_NAME");
        registerRequest.setPassword("TEST_PASSWORD");
        registerRequest.setCaptcha(captchaCodes.getCode());
        registerRequest.setCaptchaSecret(captchaCodes.getSecretCode());
        String result = testUtil.sendPost("/api/auth/register", status().isOk(), registerRequest);
        RegisterResponse response = objectMapper.readValue(result, new TypeReference<>() {});
        Optional<User> user = userRepository.findByEmail(EMAIL);

        assertThat(response.isResult()).isTrue();
        assertThat(user.isPresent()).isTrue();

        registerRequest = new RegisterRequest();
        registerRequest.setEmail(EMAIL);
        registerRequest.setName("TEST_NAME");
        registerRequest.setPassword("TEST");
        registerRequest.setCaptcha(captchaCodes.getCode());
        registerRequest.setCaptchaSecret(captchaCodes.getSecretCode());
        result = testUtil.sendPost("/api/auth/register", status().isOk(), registerRequest);
        response = objectMapper.readValue(result, new TypeReference<>() {});
        assertThat(response.isResult()).isFalse();
        assertThat(response.getErrors().getEmail()).isNotNull();
        assertThat(response.getErrors().getPassword()).isNotNull();
    }


    /**
     * Проверка аутентификации.
     * 1. С неверным паролем.
     * 2. С корректными данными.
     * */
    @Test
    public void step03_login() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(EMAIL);
        loginRequest.setPassword("TEST");
        String result = testUtil.sendPost("/api/auth/login", status().isOk(), loginRequest);
        LoginResponse response = objectMapper.readValue(result, new TypeReference<>() {});
        assertThat(response.isResult()).isFalse();

        loginRequest.setEmail(EMAIL);
        loginRequest.setPassword("TEST_PASSWORD");
        result = testUtil.sendPost("/api/auth/login", status().isOk(), loginRequest);
        response = objectMapper.readValue(result, new TypeReference<>() {});
        assertThat(response.isResult()).isTrue();
        assertThat(response.getUserLoginResponse().getEmail()).isEqualToIgnoringCase(EMAIL);
    }

    /**
     * Проверка авторизванного пользователя.
     * */
    @Test
    @WithUserDetails(EMAIL)
    public void step04_check() throws Exception {
        String result = testUtil.sendGet("/api/auth/check", status().isOk());
        LoginResponse response = objectMapper.readValue(result, new TypeReference<>() {});
        assertThat(response.isResult()).isTrue();
        assertThat(response.getUserLoginResponse().getEmail()).isEqualToIgnoringCase(EMAIL);
    }

    /**
     * Проверка логаута(фиктивная).
     * */
    @Test
    public void step05_logout() throws Exception {
        String result = testUtil.sendGet("/api/auth/logout", status().isOk());
        AuthLogoutResponse response = objectMapper.readValue(result, new TypeReference<>() {});
        assertThat(response.isResult()).isTrue();
    }

    /**
     * Проверка процедуры восстановления пароля
     * */
    @Test
    public void step06_restore() throws Exception {
        RestoreRequest restoreRequest = new RestoreRequest();
        restoreRequest.setEmail("wrong" + EMAIL);
        String result = testUtil.sendPost("/api/auth/restore", status().isOk(), restoreRequest);
        RestoreResponse response = objectMapper.readValue(result, new TypeReference<RestoreResponse>() {});
        assertThat(response.isResult()).isFalse();

        restoreRequest.setEmail(EMAIL);
        result = testUtil.sendPost("/api/auth/restore", status().isOk(), restoreRequest);
        response = objectMapper.readValue(result, new TypeReference<RestoreResponse>() {});
        assertThat(response.isResult()).isTrue();
    }

    /**
     * Проверка процедуры изменения пароля.
     * 1. Вариант рандомные данные - некорректный.
     * 2. Вариант заданные данные - корректный.
     * */
    @Test
    @WithUserDetails(EMAIL)
    public void step07_password() throws Exception {
        EasyRandom easyRandom = testUtil.getEasyRandom();
        CaptchaCodes captchaCodes = easyRandom.nextObject(CaptchaCodes.class);
        captchaCodesRepository.save(captchaCodes);

        //По емейлу находим пользователя, записываем код для восстановления пароля
        User user = userRepository.findByEmail(EMAIL).get();
        user.setCode(CODE);
        userRepository.save(user);

        easyRandom = testUtil.getEasyRandom();
        PasswordRequest wrongPasswordRequest = easyRandom.nextObject(PasswordRequest.class);

        String result = testUtil.sendPost("/api/auth/password", status().isOk(), wrongPasswordRequest);
        PasswordResponse response = objectMapper.readValue(result, new TypeReference<>() {});
        assertThat(response.isResult()).isFalse();
        assertThat(response.getErrors().getCaptcha()).isNotNull();

        PasswordRequest passwordRequest = new PasswordRequest();
        wrongPasswordRequest.setCaptcha(captchaCodes.getCode());
        wrongPasswordRequest.setCaptchaSecret(captchaCodes.getSecretCode());
        wrongPasswordRequest.setCode(CODE);
        wrongPasswordRequest.setPassword("TEST_PASSWORD");

        result = testUtil.sendPost("/api/auth/password", status().isOk(), wrongPasswordRequest);
        response = objectMapper.readValue(result, new TypeReference<>() {});
        assertThat(response.isResult()).isTrue();
        assertThat(response.getErrors()).isNull();
    }


}
