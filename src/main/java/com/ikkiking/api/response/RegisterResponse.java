package com.ikkiking.api.response;

import lombok.Data;

@Data
public class RegisterResponse {
    private boolean result;
    private RegisterErrorResponse errors;
}
