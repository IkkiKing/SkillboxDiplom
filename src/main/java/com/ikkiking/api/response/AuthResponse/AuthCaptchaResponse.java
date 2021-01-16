package com.ikkiking.api.response.AuthResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthCaptchaResponse {
    private String secret;
    private String image;

}
