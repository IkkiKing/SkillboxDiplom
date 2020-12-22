package com.ikkiking.service;

import com.ikkiking.api.response.AuthResponse.AuthCaptchaResponse;
import com.ikkiking.api.response.AuthResponse.AuthCheckResponse;
import com.ikkiking.api.response.AuthResponse.AuthLogoutResponse;
import com.ikkiking.api.response.AuthResponse.AuthUser;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public AuthCheckResponse check(){
        AuthUser user = new AuthUser(Long.valueOf(1),
                    "Вася Петров",
                    "unknown.jpg",
                "VasyaPetrog@gmail.com",
                true,
                (long) 1,
                true
                );

        return new AuthCheckResponse(true, user);
    }

    public AuthCaptchaResponse getCaptcha(){
        return new AuthCaptchaResponse("car4y8cryaw84cr89awnrc", "data:image/png;base64, код_изображения_в_base64");
    }

    public AuthLogoutResponse logout(){
        return new AuthLogoutResponse(true);
    }
}
