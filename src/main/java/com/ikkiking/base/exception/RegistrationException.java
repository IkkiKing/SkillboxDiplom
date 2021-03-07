package com.ikkiking.base.exception;

import com.ikkiking.api.response.RegisterErrorResponse;
import lombok.Data;

@Data
public class RegistrationException extends RuntimeException {
    private RegisterErrorResponse registerErrorResponse;

    public RegistrationException(RegisterErrorResponse registerErrorResponse) {
        this.registerErrorResponse = registerErrorResponse;
    }
}
