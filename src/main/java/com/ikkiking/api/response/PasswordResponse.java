package com.ikkiking.api.response;

import lombok.Data;

@Data
public class PasswordResponse {
    private boolean result;
    private PasswordErrorResponse errors;
}
