package com.ikkiking.controller;

import com.ikkiking.api.request.LoginRequest;
import com.ikkiking.api.response.AuthResponse.AuthCaptchaResponse;
import com.ikkiking.api.response.AuthResponse.AuthLogoutResponse;
import com.ikkiking.api.response.LoginResponse;
import com.ikkiking.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }

    @GetMapping("/check")
    public ResponseEntity<LoginResponse> check(Principal principal){
        return authService.check(principal);
    }


   @GetMapping("/captcha")
   public AuthCaptchaResponse getCaptcha(){
       return authService.getCaptcha();
   }


    @GetMapping("/logout")
    public AuthLogoutResponse logout(){
        return authService.logout();
    }
}
