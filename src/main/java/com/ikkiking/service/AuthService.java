package com.ikkiking.service;

import com.ikkiking.api.request.LoginRequest;
import com.ikkiking.api.request.RegisterRequest;
import com.ikkiking.api.response.AuthResponse.AuthCaptchaResponse;
import com.ikkiking.api.response.AuthResponse.AuthLogoutResponse;
import com.ikkiking.api.response.LoginResponse;
import com.ikkiking.api.response.RegisterErrorResponse;
import com.ikkiking.api.response.RegisterResponse;
import com.ikkiking.api.response.UserLoginResponse;
import com.ikkiking.base.DateHelper;
import com.ikkiking.config.SecurityConfig;
import com.ikkiking.repository.CaptchaCodesRepository;
import com.ikkiking.repository.GlobalSettingsRepository;
import com.ikkiking.repository.PostRepository;
import com.ikkiking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.DateUtils;

import java.security.Principal;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final GlobalSettingsRepository globalSettingsRepository;
    private final CaptchaCodesRepository captchaCodesRepository;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, PostRepository postRepository, GlobalSettingsRepository globalSettingsRepository, CaptchaCodesRepository captchaCodesRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.globalSettingsRepository = globalSettingsRepository;
        this.captchaCodesRepository = captchaCodesRepository;
    }

    private LoginResponse getLoginResponse(String email) {
        com.ikkiking.model.User currentUser = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setId(currentUser.getId());
        userLoginResponse.setName(currentUser.getName());
        userLoginResponse.setPhoto(currentUser.getPhoto());
        userLoginResponse.setEmail(currentUser.getEmail());

        userLoginResponse.setModeration(currentUser.isModerator());
        userLoginResponse.setSettings(currentUser.isModerator());

        if (currentUser.isModerator()) {
            userLoginResponse.setModerationCount(postRepository.countPostsForModeration());
        } else {
            userLoginResponse.setModerationCount(0);
        }

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        loginResponse.setUserLoginResponse(userLoginResponse);
        return loginResponse;
    }


    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = (User) auth.getPrincipal();
        LoginResponse loginResponse = getLoginResponse(user.getUsername());
        return ResponseEntity.ok(loginResponse);
    }

    public ResponseEntity<LoginResponse> check(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(new LoginResponse());
        }
        return ResponseEntity.ok(getLoginResponse(principal.getName()));
    }

    public AuthCaptchaResponse getCaptcha() {
        return new AuthCaptchaResponse("car4y8cryaw84cr89awnrc", "data:image/png;base64, код_изображения_в_base64");
    }

    public AuthLogoutResponse logout() {
        SecurityContextHolder.clearContext();
        return new AuthLogoutResponse(true);
    }

    private boolean isValidRegisterRequest(RegisterRequest registerRequest,
                                           RegisterErrorResponse registerErrorResponse) {

        boolean result = true;

        if (registerRequest.getEmail().trim().isEmpty() ||
                registerRequest.getEmail() == null) {
            registerErrorResponse.setEmail("E-mail не может быть пустым");
            result = false;
        } else {
            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                registerErrorResponse.setEmail("Этот e-mail уже зарегистрирован");
                result = false;
            }
        }

        if (registerRequest.getName().trim().isEmpty() ||
                registerRequest.getName() == null) {
            registerErrorResponse.setName("Имя не может быть пустым");
            result = false;
        }

        if (registerRequest.getPassword().trim().isEmpty() ||
                registerRequest.getPassword() == null) {
            registerErrorResponse.setPassword("Пароль не может быть пустым");
            result = false;
        } else {
            if (registerRequest.getPassword().length() < 6) {
                result = false;
                registerErrorResponse.setPassword("Пароль не может быть короче 6 символов");
            }
        }

        if (registerRequest.getCaptcha().trim().isEmpty() ||
                registerRequest.getCaptcha() == null) {
            registerErrorResponse.setCaptcha("Код с картинки не может быть пустым");
            result = false;
        }else {
            if (captchaCodesRepository.countByCodeAndSecretCode(
                    registerRequest.getCaptcha(),
                    registerRequest.getCaptchaSecret()
                    ) == 0){
                registerErrorResponse.setCaptcha("Код с картинки указан не верно");
                result = false;
            }
        }

        return result;
    }

    public ResponseEntity<RegisterResponse> register
            (RegisterRequest registerRequest) {
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setResult(true);

        RegisterErrorResponse registerErrorResponse = new RegisterErrorResponse();

        //Если регистрация закрыта
        if (!SettingsService.getSettingsValue(globalSettingsRepository, "MULTIUSER_MODE")) {
            return new ResponseEntity<>(registerResponse, HttpStatus.NOT_FOUND);
        }

        //Если запрос некорректный
        if (!isValidRegisterRequest(registerRequest, registerErrorResponse)) {
            registerResponse.setResult(false);
            registerResponse.setErrors(registerErrorResponse);
            return ResponseEntity.ok(registerResponse);
        }
        com.ikkiking.model.User user = new com.ikkiking.model.User();
        user.setModerator(false);
        user.setRegTime(DateHelper.getCurrentDate().getTime());
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        /*TODO:
        * проверить как кодируется пароль
        * */
        user.setPassword(SecurityConfig
                .passwordEncoder()
                .encode(registerRequest.getPassword()));
        return ResponseEntity.ok(registerResponse);
    }
}
