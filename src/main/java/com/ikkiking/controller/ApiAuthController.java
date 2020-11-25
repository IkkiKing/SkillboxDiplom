package com.ikkiking.controller;

import com.ikkiking.api.response.CheckResponse.AuthCheckResponse;
import com.ikkiking.service.AuthCheckService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiAuthController {

    private final AuthCheckService authCheckService;

    public ApiAuthController(AuthCheckService authCheckService) {
        this.authCheckService = authCheckService;
    }

    @GetMapping("/api/auth/check")
    private AuthCheckResponse check(){
        return authCheckService.check();
    }

}
