package com.ikkiking.base.exception;

import com.ikkiking.api.response.post.PostErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostException extends RuntimeException {
    private PostErrorResponse postErrorResponse;
}
