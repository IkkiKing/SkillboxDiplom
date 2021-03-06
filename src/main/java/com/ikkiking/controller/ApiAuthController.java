package com.ikkiking.controller;

import com.ikkiking.api.request.LoginRequest;
import com.ikkiking.api.request.PasswordRequest;
import com.ikkiking.api.request.RegisterRequest;
import com.ikkiking.api.request.RestoreRequest;
import com.ikkiking.api.response.auth.AuthCaptchaResponse;
import com.ikkiking.api.response.auth.AuthLogoutResponse;
import com.ikkiking.api.response.LoginResponse;
import com.ikkiking.api.response.PasswordResponse;
import com.ikkiking.api.response.RegisterResponse;
import com.ikkiking.api.response.RestoreResponse;
import com.ikkiking.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthService authService;

    @Autowired
    public ApiAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @GetMapping("/check")
    public ResponseEntity<LoginResponse> check(Principal principal) {
        return authService.check(principal);
    }

    @GetMapping("/captcha")
    public ResponseEntity<AuthCaptchaResponse> captcha() {
        return authService.captcha();
    }

    @GetMapping("/logout")
    public ResponseEntity<AuthLogoutResponse> logout() {
        return authService.logout();
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/restore")
    public ResponseEntity<RestoreResponse> register(@RequestBody RestoreRequest restoreRequest) {
        return authService.restore(restoreRequest);
    }

    @PostMapping("/password")
    public ResponseEntity<PasswordResponse> password(@RequestBody PasswordRequest passwordRequest) {
        return authService.password(passwordRequest);
    }


}
