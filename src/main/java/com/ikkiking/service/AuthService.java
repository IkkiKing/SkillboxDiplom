package com.ikkiking.service;

import com.ikkiking.api.request.LoginRequest;
import com.ikkiking.api.request.PasswordRequest;
import com.ikkiking.api.request.RegisterRequest;
import com.ikkiking.api.request.RestoreRequest;
import com.ikkiking.api.response.AuthResponse.AuthCaptchaResponse;
import com.ikkiking.api.response.AuthResponse.AuthLogoutResponse;
import com.ikkiking.api.response.*;
import com.ikkiking.base.CaptchaUtil;
import com.ikkiking.base.DateHelper;
import com.ikkiking.base.ImageUtil;
import com.ikkiking.base.exception.*;
import com.ikkiking.config.SecurityConfig;
import com.ikkiking.model.CaptchaCodes;
import com.ikkiking.repository.CaptchaCodesRepository;
import com.ikkiking.repository.GlobalSettingsRepository;
import com.ikkiking.repository.PostRepository;
import com.ikkiking.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.Principal;
import java.util.Optional;

@Service
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final GlobalSettingsRepository globalSettingsRepository;
    private final CaptchaCodesRepository captchaCodesRepository;
    private final JavaMailSender emailSender;
    @Value("${authService.captchaDeleteHours}")
    private static int captchaDeleteHours;


    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       PostRepository postRepository,
                       GlobalSettingsRepository globalSettingsRepository,
                       CaptchaCodesRepository captchaCodesRepository,
                       JavaMailSender emailSender) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.globalSettingsRepository = globalSettingsRepository;
        this.captchaCodesRepository = captchaCodesRepository;
        this.emailSender = emailSender;
    }

    /**
     * Аутентификация - логин.
     * */
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {

        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = (User) auth.getPrincipal();
        LoginResponse loginResponse = getLoginResponse(user.getUsername());

        return ResponseEntity.ok(loginResponse);
    }

    /**
     * Проверка статуса авторизации.
     * */
    public ResponseEntity<LoginResponse> check(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(new LoginResponse());
        }
        return ResponseEntity.ok(getLoginResponse(principal.getName()));
    }

    /**
     * Вспомогательный метод формирования LoginResponse по email пользователя.
     * */
    private LoginResponse getLoginResponse(String email) {

        com.ikkiking.model.User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setId(currentUser.getId());
        userLoginResponse.setName(currentUser.getName());
        userLoginResponse.setPhoto(currentUser.getPhoto());
        userLoginResponse.setEmail(currentUser.getEmail());
        userLoginResponse.setModeration(currentUser.isModerator());
        userLoginResponse.setSettings(currentUser.isModerator());
        userLoginResponse.setModerationCount(
                currentUser.isModerator() ? postRepository.countPostsForModeration() : 0);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        loginResponse.setUserLoginResponse(userLoginResponse);
        return loginResponse;
    }

    /**
     * Формирование капчи.
     * */
    @Transactional
    public ResponseEntity<AuthCaptchaResponse> captcha() {
        CaptchaUtil captchaUtil = new CaptchaUtil(4,100, 35);
        String code = captchaUtil.getCode();
        String secretCode = captchaUtil.getSecretCode();

        AuthCaptchaResponse authCaptchaResponse = new AuthCaptchaResponse();
        authCaptchaResponse.setSecret(secretCode);
        authCaptchaResponse.setImage(captchaUtil.getImageString());

        CaptchaCodes captchaCodes = new CaptchaCodes();
        captchaCodes.setCode(code);
        captchaCodes.setSecretCode(secretCode);
        captchaCodes.setTime(DateHelper.getCurrentDate().getTime());
        //Удаляем устаревшие капчи
        captchaCodesRepository.deleteOldCaptcha(captchaDeleteHours);
        //Сохраняем новую
        captchaCodesRepository.save(captchaCodes);
        log.info("Captcha code is: " + code + ". Secret code is: " + secretCode);
        return ResponseEntity.ok(authCaptchaResponse);
    }

    /**
     * Логаут.
     * */
    public ResponseEntity<AuthLogoutResponse> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new AuthLogoutResponse(true));
    }

    /**
     * Регистрация.
     * */
    @Transactional
    public ResponseEntity<RegisterResponse> register(RegisterRequest registerRequest) {
        //Валидация корректности запроса
        validateRegisterRequest(registerRequest);

        com.ikkiking.model.User user = new com.ikkiking.model.User();
        user.setModerator(false);
        user.setRegTime(DateHelper.getCurrentDate().getTime());
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(SecurityConfig
                .passwordEncoder()
                .encode(registerRequest.getPassword()));

        userRepository.save(user);
        return ResponseEntity.ok(new RegisterResponse(true));
    }

    /**
     * Вспомогательный метод проверки валидности регистрационных данных.
     * */
    private void validateRegisterRequest(RegisterRequest registerRequest) {
        RegisterErrorResponse registerErrorResponse = new RegisterErrorResponse();

        //Если регистрация закрыта
        if (!SettingsService.getSettingsValue(globalSettingsRepository, "MULTIUSER_MODE")) {
            throw new RegistrationClosedException("Register is closed");
        }
        if (registerRequest.getEmail().isEmpty() || registerRequest.getEmail() == null) {
            registerErrorResponse.setEmail("E-mail не может быть пустым");
            throw new RegistrationException(registerErrorResponse);
        }
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            registerErrorResponse.setEmail("Этот e-mail уже зарегистрирован");
            throw new RegistrationException(registerErrorResponse);
        }
        if (registerRequest.getName().isEmpty() || registerRequest.getName() == null) {
            registerErrorResponse.setName("Имя не может быть пустым");
            throw new RegistrationException(registerErrorResponse);
        }
        if (registerRequest.getPassword().isEmpty() ||registerRequest.getPassword() == null) {
            registerErrorResponse.setPassword("Пароль не может быть пустым");
            throw new RegistrationException(registerErrorResponse);
        }
        if (registerRequest.getPassword().length() < 6) { //TODO: move in config variable
            registerErrorResponse.setPassword("Пароль не может быть короче 6 символов");
            throw new RegistrationException(registerErrorResponse);
        }
        if (registerRequest.getCaptcha().isEmpty() || registerRequest.getCaptcha() == null) {
            registerErrorResponse.setCaptcha("Код с картинки не может быть пустым");
            throw new RegistrationException(registerErrorResponse);
        }

        if (captchaCodesRepository.countByCodeAndSecretCode(registerRequest.getCaptcha(),
                                                            registerRequest.getCaptchaSecret()) == 0) {
            registerErrorResponse.setCaptcha("Код с картинки указан не верно");
            throw new RegistrationException(registerErrorResponse);
        }
    }

    /**
     * Восстановление пароля.
     * */
    @Transactional
    public ResponseEntity<RestoreResponse> restore(RestoreRequest restoreRequest) {
        RestoreResponse restoreResponse = new RestoreResponse();
        Optional<com.ikkiking.model.User> userOptional = userRepository.findByEmail(restoreRequest.getEmail());

        if (userOptional.isPresent()) {
            com.ikkiking.model.User user = userOptional.get();
            String userCode = RandomStringUtils.random(40, true, true);//TODO: 40 move in config variable
            user.setCode(userCode);
            userRepository.save(user);
            restoreResponse.setResult(true);
            ImageUtil.MailUtil.sendMail(emailSender,
                    restoreRequest.getEmail(),
                    "Восстановление пароля DevPub",
                    "Для восстановления вашего пароля, пройдите по ссылке " +
                            "http://localhost:8080/login/change-password/" + userCode);

        }else{
            log.warn("User not found. Email wasnt sended.");
        }
        return ResponseEntity.ok(restoreResponse);
    }

    /**
     * Изменение пароля.
     * */
    @Transactional
    public ResponseEntity<PasswordResponse> password(PasswordRequest passwordRequest) {
        PasswordResponse passwordResponse = new PasswordResponse();

        validateRestorePasswordRequest(passwordRequest);

        Optional<com.ikkiking.model.User> userOptional =
                userRepository.findByCode(passwordRequest.getCode());

        if (userOptional.isPresent()) {
            com.ikkiking.model.User user = userOptional.get();
            user.setPassword(SecurityConfig.passwordEncoder()
                    .encode(passwordRequest.getPassword()));
            user.setCode(null);
            userRepository.save(user);
        }else{
            log.warn("The link for restore password is too old.");
            PasswordErrorResponse passwordErrorResponse = new PasswordErrorResponse();
            passwordErrorResponse.setCode("Ссылка для восстановления пароля устарела." +
                    "<a href =\"/auth/restore\">Запросить ссылку снова</a>");
            throw new PasswordRestoreException(passwordErrorResponse);
        }
        return ResponseEntity.ok(passwordResponse);
    }

    /**
     * Вспомогательный метод проверки валидности регистрационных данных.
     * */
    private void validateRestorePasswordRequest(PasswordRequest passwordRequest) {
        PasswordErrorResponse passwordErrorResponse = new PasswordErrorResponse();

        String password = passwordRequest.getPassword();

        if (password.isEmpty() || password == null) {
            passwordErrorResponse.setPassword("Пароль не может быть пустым");
            throw new PasswordRestoreException(passwordErrorResponse);
        }

        if (password.length() < 6) {//TODO: move in config variable
            passwordErrorResponse.setPassword("Пароль короче 6 символов");
            throw new PasswordRestoreException(passwordErrorResponse);
        }

        if (captchaCodesRepository.countByCodeAndSecretCode(
                passwordRequest.getCaptcha(),
                passwordRequest.getCaptchaSecret()) == 0) {

            passwordErrorResponse.setCaptcha("Код с картинки введён неверно");
            throw new PasswordRestoreException(passwordErrorResponse);
        }
    }
}
