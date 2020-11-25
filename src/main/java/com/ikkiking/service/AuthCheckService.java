package com.ikkiking.service;

import com.ikkiking.api.response.CheckResponse.AuthCheckResponse;
import com.ikkiking.api.response.CheckResponse.User;
import org.springframework.stereotype.Service;

@Service
public class AuthCheckService {

    public AuthCheckResponse check(){
        User user = new User(1,
                    "Вася Петров",
                    "unknown.jpg",
                "VasyaPetrog@gmail.com",
                true,
                1,
                true
                );

        return new AuthCheckResponse(true, user);
    }
}
