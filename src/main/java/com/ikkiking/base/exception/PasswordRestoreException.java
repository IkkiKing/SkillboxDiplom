package com.ikkiking.base.exception;

import com.ikkiking.api.response.PasswordErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRestoreException extends RuntimeException {
    private PasswordErrorResponse passwordErrorResponse;
}
