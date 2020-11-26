package com.ikkiking.controller;

import com.ikkiking.api.response.AuthResponse.AuthCaptchaResponse;
import com.ikkiking.api.response.AuthResponse.AuthCheckResponse;
import com.ikkiking.api.response.AuthResponse.AuthLogoutResponse;
import com.ikkiking.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthService authService;

    public ApiAuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/check")
    private AuthCheckResponse check(){
        return authService.check();
    }


   @GetMapping("/captcha")
   private AuthCaptchaResponse getCaptcha(){
       return authService.getCaptcha();
   }


    @GetMapping("/logout")
    private AuthLogoutResponse logout(){
        return authService.logout();
    }
}
