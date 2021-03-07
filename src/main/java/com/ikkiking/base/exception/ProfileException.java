package com.ikkiking.base.exception;

import com.ikkiking.api.response.ProfileErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileException extends RuntimeException {
    private ProfileErrorResponse profileErrorResponse;
}
