package com.ikkiking.api.response.post;

import lombok.Data;

@Data
public class PostReturnResponse {
    private boolean result;
    private PostErrorResponse errors;
}
