package com.ikkiking.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private boolean result;
    private RegisterErrorResponse errors;

    public RegisterResponse(boolean result) {
        this.result = result;
    }

}
