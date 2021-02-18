package com.ikkiking.api.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileResponse {
    private boolean result;
    private ProfileErrorResponse errors;
}
