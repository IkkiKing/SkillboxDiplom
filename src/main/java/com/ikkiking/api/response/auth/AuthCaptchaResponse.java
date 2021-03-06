package com.ikkiking.api.response.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthCaptchaResponse {
    private String secret;
    private String image;

}
