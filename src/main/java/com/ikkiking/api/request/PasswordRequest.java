package com.ikkiking.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRequest {
    //TEST 2

    private String code;
    private String password;
    private String captcha;
    @JsonProperty(value = "captcha_secret")
    private String captchaSecret;
}
