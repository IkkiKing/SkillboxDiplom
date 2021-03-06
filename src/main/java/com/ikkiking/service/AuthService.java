package com.ikkiking.service;

import com.ikkiking.api.request.LoginRequest;
import com.ikkiking.api.request.PasswordRequest;
import com.ikkiking.api.request.RegisterRequest;
import com.ikkiking.api.request.RestoreRequest;
import com.ikkiking.api.response.LoginResponse;
import com.ikkiking.api.response.PasswordErrorResponse;
import com.ikkiking.api.response.PasswordResponse;
import com.ikkiking.api.response.RegisterErrorResponse;
import com.ikkiking.api.response.RegisterResponse;
import com.ikkiking.api.response.RestoreResponse;
import com.ikkiking.api.response.UserLoginResponse;
import com.ikkiking.api.response.auth.AuthCaptchaResponse;
import com.ikkiking.api.response.auth.AuthLogoutResponse;
import com.ikkiking.base.CaptchaUtil;
import com.ikkiking.base.DateHelper;
import com.ikkiking.base.exception.PasswordRestoreException;
import com.ikkiking.base.exception.RegistrationClosedException;
import com.ikkiking.base.exception.RegistrationException;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.Principal;
import java.util.Calendar;
import java.util.Optional;

@Service
@Slf4j
public class AuthService {

    @Value("${captcha.delete.hours}")
    private int captchaDeleteHours;

    @Value("${password.min.length}")
    private int passwordMinLength;

    @Value("${password.restore.code.length}")
    private int passwordRestoreCodeLength;

    @Value("${captcha.length}")
    private int captchaLength;

    @Value("${captcha.width}")
    private int captchaWidth;

    @Value("${captcha.height}")
    private int captchaHeight;

    @Value("${captcha.secretCode.length}")
    private int captchaSecretCodeLength;

    @Value("${blog.url}")
    private String blogUrl;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final GlobalSettingsRepository globalSettingsRepository;
    private final CaptchaCodesRepository captchaCodesRepository;
    private final MailService mailService;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       PostRepository postRepository,
                       GlobalSettingsRepository globalSettingsRepository,
                       CaptchaCodesRepository captchaCodesRepository,
                       MailService mailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.globalSettingsRepository = globalSettingsRepository;
        this.captchaCodesRepository = captchaCodesRepository;
        this.mailService = mailService;
    }

    /**
     * Аутентификация - логин.
     */
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
     */
    public ResponseEntity<LoginResponse> check(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(new LoginResponse());
        }
        return ResponseEntity.ok(getLoginResponse(principal.getName()));
    }

    /**
     * Вспомогательный метод формирования LoginResponse по email пользователя.
     */
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

        return new LoginResponse(true, userLoginResponse);
    }

    /**
     * Формирование капчи.
     */
    @Transactional
    public ResponseEntity<AuthCaptchaResponse> captcha() {
        CaptchaUtil captchaUtil = new CaptchaUtil(
                captchaLength,
                captchaWidth,
                captchaHeight,
                captchaSecretCodeLength);
        String code = captchaUtil.getCode();
        String secretCode = captchaUtil.getSecretCode();

        AuthCaptchaResponse authCaptchaResponse = new AuthCaptchaResponse();
        authCaptchaResponse.setSecret(secretCode);
        authCaptchaResponse.setImage(captchaUtil.getImageString());

        CaptchaCodes captchaCodes = new CaptchaCodes();
        captchaCodes.setCode(code);
        captchaCodes.setSecretCode(secretCode);
        captchaCodes.setTime(DateHelper.getCurrentDate().getTime());
        deleteOldCaptcha();
        //Сохраняем новую
        captchaCodesRepository.save(captchaCodes);
        log.info("Captcha code is: " + code + ". Secret code is: " + secretCode);
        return ResponseEntity.ok(authCaptchaResponse);
    }

    /**
     * Метод удаления старой капчи.
     */
    private void deleteOldCaptcha() {
        Calendar calendar = DateHelper.getCurrentDate();
        calendar.add(Calendar.HOUR, -captchaDeleteHours);
        //Удаляем устаревшие капчи
        captchaCodesRepository.deleteOldCaptcha(calendar.getTime());
    }

    /**
     * Логаут.
     */
    public ResponseEntity<AuthLogoutResponse> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(new AuthLogoutResponse(true));
    }

    /**
     * Регистрация.
     */
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
     */
    private void validateRegisterRequest(RegisterRequest registerRequest) {
        RegisterErrorResponse registerErrorResponse = new RegisterErrorResponse();
        //Если регистрация закрыта
        if (!SettingsService.getSettingsValue(globalSettingsRepository, "MULTIUSER_MODE")) {
            throw new RegistrationClosedException("Register is closed");
        }
        if (registerRequest.getEmail().isEmpty() || registerRequest.getEmail() == null) {
            registerErrorResponse.setEmail("E-mail не может быть пустым");
        } else {
            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                registerErrorResponse.setEmail("Этот e-mail уже зарегистрирован");
            }
        }
        if (registerRequest.getName().isEmpty() || registerRequest.getName() == null) {
            registerErrorResponse.setName("Имя не может быть пустым");
        }
        if (registerRequest.getPassword().isEmpty() || registerRequest.getPassword() == null) {
            registerErrorResponse.setPassword("Пароль не может быть пустым");
        } else {
            if (registerRequest.getPassword().length() < passwordMinLength) {
                registerErrorResponse.setPassword("Пароль не может быть короче 6 символов");
            }
        }
        if (registerRequest.getCaptcha().isEmpty() || registerRequest.getCaptcha() == null) {
            registerErrorResponse.setCaptcha("Код с картинки не может быть пустым");
        } else {
            if (captchaCodesRepository.countByCodeAndSecretCode(registerRequest.getCaptcha(),
                    registerRequest.getCaptchaSecret()) == 0) {
                registerErrorResponse.setCaptcha("Код с картинки указан не верно");
            }
        }
        if (registerErrorResponse.getEmail() != null
                || registerErrorResponse.getName() != null
                || registerErrorResponse.getPassword() != null
                || registerErrorResponse.getCaptcha() != null) {
            throw new RegistrationException(registerErrorResponse);
        }
    }

    /**
     * Восстановление пароля.
     */
    @Transactional
    public ResponseEntity<RestoreResponse> restore(RestoreRequest restoreRequest) {
        RestoreResponse restoreResponse = new RestoreResponse();
        Optional<com.ikkiking.model.User> userOptional = userRepository.findByEmail(restoreRequest.getEmail());

        if (userOptional.isPresent()) {
            com.ikkiking.model.User user = userOptional.get();
            String userCode = RandomStringUtils.random(passwordRestoreCodeLength, true, true);
            user.setCode(userCode);
            userRepository.save(user);
            mailService.send(restoreRequest.getEmail(),
                    "Восстановление пароля DevPub",
                    "Для восстановления вашего пароля, пройдите по ссылке "
                            + "https://" + blogUrl + "/login/change-password/" + userCode);
            restoreResponse.setResult(true);
        } else {
            log.warn("User not found. Email wasnt sended.");
        }
        return ResponseEntity.ok(restoreResponse);
    }

    /**
     * Изменение пароля.
     */
    @Transactional
    public ResponseEntity<PasswordResponse> password(PasswordRequest passwordRequest) {

        validateRestorePasswordRequest(passwordRequest);

        Optional<com.ikkiking.model.User> userOptional =
                userRepository.findByCode(passwordRequest.getCode());

        if (userOptional.isPresent()) {
            com.ikkiking.model.User user = userOptional.get();
            user.setPassword(SecurityConfig.passwordEncoder()
                    .encode(passwordRequest.getPassword()));
            user.setCode(null);
            userRepository.save(user);
        } else {
            log.warn("The link for restore password is too old.");
            PasswordErrorResponse passwordErrorResponse = new PasswordErrorResponse();
            passwordErrorResponse.setCode("Ссылка для восстановления пароля устарела."
                    + "<a href =\"/auth/restore\">Запросить ссылку снова</a>");
            throw new PasswordRestoreException(passwordErrorResponse);
        }
        PasswordResponse passwordResponse = new PasswordResponse();
        passwordResponse.setResult(true);
        return ResponseEntity.ok(passwordResponse);
    }

    /**
     * Вспомогательный метод проверки валидности регистрационных данных.
     */
    private void validateRestorePasswordRequest(PasswordRequest passwordRequest) {
        PasswordErrorResponse passwordErrorResponse = new PasswordErrorResponse();

        String password = passwordRequest.getPassword();
        if (password.isEmpty() || password == null) {
            passwordErrorResponse.setPassword("Пароль не может быть пустым");
        } else {
            if (password.length() < passwordMinLength) {
                passwordErrorResponse.setPassword("Пароль короче " + passwordMinLength + " символов");
            }
        }

        if (captchaCodesRepository.countByCodeAndSecretCode(
                passwordRequest.getCaptcha(),
                passwordRequest.getCaptchaSecret()) == 0) {
            passwordErrorResponse.setCaptcha("Код с картинки введён неверно");
        }

        if (passwordErrorResponse.getPassword() != null
                || passwordErrorResponse.getCaptcha() != null) {
            throw new PasswordRestoreException(passwordErrorResponse);
        }
    }
}
