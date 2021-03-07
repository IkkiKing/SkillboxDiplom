package com.ikkiking.api.response.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostReturnResponse {
    private boolean result;
    private PostErrorResponse errors;

    public PostReturnResponse(boolean result) {
        this.result = result;
    }
}
