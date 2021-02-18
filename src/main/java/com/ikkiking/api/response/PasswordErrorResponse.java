package com.ikkiking.api.response;

import lombok.Data;

@Data
public class PasswordErrorResponse {
    private String code;
    private String password;
    private String captcha;
}
