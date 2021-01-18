package com.ikkiking.api.response.PostResponse;

import lombok.Data;

@Data
public class PostReturnResponse {
    private boolean result;
    private ErrorMessageResponse errors;
}
